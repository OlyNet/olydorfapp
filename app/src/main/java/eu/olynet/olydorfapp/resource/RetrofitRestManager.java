/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */

class RetrofitRestManager extends RestManager {

    /**
     * The Retrofit Service object which does the actual HTTP requests.
     */
    private OlyNetService ons;

    /**
     * The User-Agent String. Will be filled on first use. <b>Do not</b> use directly, call
     * getUserAgent() instead.
     */
    private String userAgent = null;

    /**
     * Sets up the RetrofitRestManager.
     *
     * @param context the application Context.
     */
    RetrofitRestManager(Context context) {
        super(context);
    }

    /**
     * Sets up the Retrofit client. <br/>
     * <b>ONLY MESS WITH THIS IF YOU KNOW EXACTLY WHAT YOU ARE DOING!</b>
     */
    @Override
    protected void init() {
        try {
            /* basic setup for certificate loading */
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();

            /*
             * create a X509TrustManager that contains the OlyNet e.V. custom CA.
             *
             * See: http://stackoverflow.com/q/27562666/3997552
             */
            InputStream ca = this.context.getAssets().open(Configuration.CA_FILE);
            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            trustStore.load(null);
            Certificate caCert = cf.generateCertificate(ca);
            trustStore.setCertificateEntry("OlyNet e.V. Certificate Authority", caCert);
            CustomTrustManager tm = new CustomTrustManager(trustStore);
            ca.close();

            /* create a KeyManagerFactory that contains our client certificate */
            InputStream clientCert = this.context.getAssets().open(Configuration.CERTIFICATE_FILE);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(clientCert, Configuration.CERTIFICATE_KEY);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(keyStore, Configuration.CERTIFICATE_KEY);
            clientCert.close();

            /* instantiate our SSLContext with the trust store and the key store */
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);

            /* create the OKHttpClient that will be used for all queries */
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), tm)
                    .build();

            /* create the Retrofit instance with the correct configuration */
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Configuration.SERVER_BASE_URL + "/")
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(client)
                    .build();

            /* finally create the actual service */
            this.ons = retrofit.create(OlyNetService.class);
        } catch (Exception e) {
            throw new RuntimeException("Retrofit setup failed", e);
        }

    }

    /**
     * @return the 'User-Agent' header content.
     */
    private String getUserAgent() {
        if (this.userAgent == null) {
            PackageInfo info;
            try {
                info = this.context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            this.userAgent = "OlydorfApp Android " + info.versionName;
        }

        return this.userAgent;
    }

    /**
     * Ensures that the device is connected to the internet.
     *
     * @throws NoConnectionException if no connection could be detected.
     */
    private void verifyConnectivity() throws NoConnectionException {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (!cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                throw new NoConnectionException("No internet connection detected");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NoConnectionException("No internet connection detected", e);
        }
    }

    @Override
    public byte[] fetchImage(String type, int id, String field)
            throws NoConnectionException, ClientCertificateInvalidException {
        return fetchImage(type, id, field, 3);
    }

    @Override
    @SuppressWarnings("unchecked")
    public byte[] fetchImage(String type, int id, String field, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        byte[] image = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Call call = this.ons.getImage(getUserAgent(), type, id, field);

                Response<ResponseBody> response = call.execute();
                int code = response.code();

                if (code == 200) {
                    image = response.body().bytes();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: image '" + type + "' - '" + id + "' - '" +
                                             field + "'");
                    // TODO: implement better logging
                    break;
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager", "HTTP " + code + ": image '" + type + "' - '" + id +
                                             "' - '" + field + "'");
                } else {
                    Log.e("ResourceManager", "Unexpected HTTP " + code + ": image '" + type +
                                             "' - '" + id + "' - '" + field + "'");
                }

                if (image != null) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("fetchImage '" + type + "' - '" + id + "' - '" +
                                           field + "'", e);
            }
        }

        /* return the result that may still be null */
        return image;
    }

    @Override
    public AbstractMetaItem<?> fetchItem(Class clazz, int id)
            throws NoConnectionException, ClientCertificateInvalidException, Http404Exception {
        return fetchItem(clazz, id, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchItem(Class clazz, int id, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException, Http404Exception {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        AbstractMetaItem<?> result = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Class proxyClass = this.ons.getClass();
                Method getResource = proxyClass.getMethod(methodName, String.class, int.class);
                Call call = (Call) getResource.invoke(this.ons, getUserAgent(), id);

                Response response = call.execute();
                int code = response.code();

                if (code == 200) {
                    result = (AbstractMetaItem<?>) response.body();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: item '" + clazz + "' - '" + id + "'");
                    throw new Http404Exception();
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager", "HTTP " + code + ": item '" + clazz + "' - '" + id +
                                             "'");
                } else {
                    Log.e("ResourceManager", "Unexpected HTTP " + code + ": item '" + clazz +
                                             "' - '" + id + "'");
                }

                if (result != null) {
                    break;
                }
            } catch (Http404Exception e) {
                throw new Http404Exception();
            } catch (Exception e) {
                throw new RuntimeException("fetchItem '" + clazz + "' - '" + id + "'", e);
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz)
            throws NoConnectionException, ClientCertificateInvalidException {
        return fetchItems(clazz, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Class proxyClass = this.ons.getClass();
                Method getResource = proxyClass.getMethod(methodName, String.class);
                Call call = (Call) getResource.invoke(this.ons, getUserAgent());

                Response response = call.execute();
                int code = response.code();

                if (code == 200) {
                    result = (List<AbstractMetaItem<?>>) response.body();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: items '" + clazz + "'");
                    // TODO: implement better logging
                    break;
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager", "HTTP " + code + ": tems '" + clazz + "'");
                } else {
                    Log.e("ResourceManager", "Unexpected HTTP " + code + ": items '" + clazz + "'");
                }

                if (result != null) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("fetchItems '" + clazz + "'", e);
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids)
            throws NoConnectionException, ClientCertificateInvalidException {
        return fetchItems(clazz, ids, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* build the MultiID object */
        MultiID idsObj = new MultiID(ids);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Class proxyClass = this.ons.getClass();
                Method getResource = proxyClass.getMethod(methodName, String.class, MultiID.class);
                Call call = (Call) getResource.invoke(this.ons, getUserAgent(), idsObj);

                Response response = call.execute();
                int code = response.code();

                if (code == 200) {
                    result = (List<AbstractMetaItem<?>>) response.body();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: items '" + clazz + "' - '" + ids + "'");
                    // TODO: implement better logging
                    break;
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager",
                          "HTTP " + code + ": items '" + clazz + "' - '" + ids + "'");
                } else {
                    Log.e("ResourceManager",
                          "Unexpected HTTP " + code + ": items '" + clazz + "' - '" + ids + "'");
                }

                if (result != null) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("fetchItems '" + clazz + "' - '" + ids + "'", e);
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id)
            throws NoConnectionException, ClientCertificateInvalidException, Http404Exception {
        return fetchMetaItem(clazz, id, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException, Http404Exception {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + "Meta" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchMetaItem", methodName);

        /* dynamically invoke the correct Method */
        AbstractMetaItem<?> result = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Class proxyClass = this.ons.getClass();
                Method getMetaResource = proxyClass.getMethod(methodName, String.class, int.class);
                Call call = (Call) getMetaResource.invoke(this.ons, getUserAgent(), id);

                Response response = call.execute();
                int code = response.code();

                if (code == 200) {
                    result = (AbstractMetaItem<?>) response.body();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: meta '" + clazz + "' - " + id + "'");
                    throw new Http404Exception();
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager",
                          "HTTP " + code + ": meta '" + clazz + "' - " + id + "'");
                } else {
                    Log.e("ResourceManager",
                          "Unexpected HTTP " + code + ": meta '" + clazz + "' - " + id + "'");
                }

                if (result != null) {
                    break;
                }
            } catch (Http404Exception e) {
                throw new Http404Exception();
            } catch (Exception e) {
                throw new RuntimeException("fetchMetaItem '" + clazz + "' - " + id + "'", e);
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz)
            throws NoConnectionException, ClientCertificateInvalidException {
        return fetchMetaItems(clazz, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz, int retryCount)
            throws NoConnectionException, ClientCertificateInvalidException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + "Meta" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchMetaItems", methodName);

        List<AbstractMetaItem<?>> result = null;
        for (int i = 1; i <= retryCount; i++) {
            /* dynamically invoke the correct Method */
            try {
                Class proxyClass = this.ons.getClass();
                Method getMetaResources = proxyClass.getMethod(methodName, String.class);
                Call call = (Call) getMetaResources.invoke(this.ons, getUserAgent());

                Response response = call.execute();
                int code = response.code();

                if (code == 200) {
                    result = (List<AbstractMetaItem<?>>) response.body();
                } else if (code == 404) {
                    Log.e("ResourceManager", "HTTP 404: meta '" + clazz + "'");
                    // TODO: implement better logging
                    break;
                } else if (code >= 500 && code < 600) {
                    Log.e("ResourceManager", "HTTP " + code + ": meta '" + clazz + "'");
                } else {
                    Log.e("ResourceManager", "Unexpected HTTP " + code + ": meta '" + clazz + "'");
                }

                if (result != null) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException("fetchMetaItems '" + clazz + "'", e);
            }
        }

        /* return the result that may still be null */
        return result;
    }
}
