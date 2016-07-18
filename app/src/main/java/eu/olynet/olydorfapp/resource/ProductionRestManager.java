package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.NotFoundException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ProductionRestManager extends RestManager {

    /**
     * The instanced ResteasyClient.
     */
    private OlyNetClient onc;

    /**
     * Sets up the ProductionRestManager.
     *
     * @param context the application Context.
     */
    public ProductionRestManager(Context context) {
        super(context);
    }

    /**
     * Sets up the ResteasyClient. <br/>
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

            /* instantiate the HttpEngine we are going to use */
            HackedURLConnectionEngine engine = new HackedURLConnectionEngine();
            engine.setConnectionTimeout(5000);
            engine.setNotFuckedUpSslContext(sslContext);

            /* instantiate the ResteasyClient */
            ResteasyClient client = new ResteasyClientBuilder().providerFactory(
                    new ResteasyProviderFactory().register(JacksonJsonProvider.class))
                                                               .connectionPoolSize(4)
                                                               .connectionTTL(5, TimeUnit.MINUTES)
                                                               .httpEngine(engine)
                                                               .build();

            this.onc = client.target(Configuration.SERVER_BASE_URL).proxy(OlyNetClient.class);

            Log.d("ResourceManager.init", "ResteasyClient setup complete.");
        } catch (Exception e) {
            throw new RuntimeException("ResteasyClient setup failed", e);
        }
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
    public AbstractMetaItem<?> fetchItem(Class clazz, int id) throws NoConnectionException {
        return fetchItem(clazz, id, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchItem(Class clazz, int id, int retryCount) throws
                                                                              NoConnectionException {
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
                Class proxyClass = this.onc.getClass();
                Method getResource = proxyClass.getMethod(methodName, int.class);
                result = (AbstractMetaItem) getResource.invoke(this.onc, id);
                if (result != null) {
                    break;
                }
            } catch (InvocationTargetException e) {
                result = null;
                Throwable cause = e.getCause();

                /* HTTP 404 */
                if (cause != null && cause instanceof NotFoundException) {
                    Log.i("ResourceManager", "HTTP 404: '" + clazz + "' with id " + id, cause);
                    throw new NotFoundException("HTTP 404: '" + clazz + "' with id " + id, cause);
                } else {
                    System.err.println("class=" + clazz + " id=" + id);
                    e.printStackTrace();
                    // FIXME remove debug (2 lines)
                    result = null;
                    break;
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz) throws NoConnectionException {
        return fetchItems(clazz, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, int retryCount) throws
                                                                             NoConnectionException {
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
                Class proxyClass = this.onc.getClass();
                Method getResource = proxyClass.getMethod(methodName);
                result = (List<AbstractMetaItem<?>>) getResource.invoke(this.onc);
                if (result != null) {
                    break;
                }
            } catch (InvocationTargetException e) {
                result = null;
                Throwable cause = e.getCause();

                /* HTTP 404 */
                if (cause != null && cause instanceof NotFoundException) {
                    Log.e("ResourceManager", "HTTP 404: '" + clazz + "'", cause);
                    // TODO: implement better logging
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids) throws NoConnectionException {
        return fetchItems(clazz, ids, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids, int retryCount) throws
                                                                             NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = ResourceManager.getResourceString(clazz);

        /* build the query string containing all ids */
        String query = "";
        for (Integer id : ids) {
            if (id == null) {
                throw new NullPointerException("the List of ids may not contain null elements");
            }

            /* add escaped semicolon if the query is not empty */
            if (!query.equals("")) {
                query += "%3B";
            }

            query += id;
        }

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result = null;
        for (int i = 1; i <= retryCount; i++) {
            try {
                Class proxyClass = this.onc.getClass();
                Method getResource = proxyClass.getMethod(methodName, String.class);
                result = (List<AbstractMetaItem<?>>) getResource.invoke(this.onc, query);
                if (result != null) {
                    break;
                }
            } catch (InvocationTargetException e) {
                result = null;
                Throwable cause = e.getCause();

                /* HTTP 404 */
                if (cause != null && cause instanceof NotFoundException) {
                    Log.e("ResourceManager", "HTTP 404: '" + clazz + "'", cause);
                    // TODO: implement better logging
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id) throws NoConnectionException {
        return fetchMetaItem(clazz, id, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id, int retryCount) throws
                                                                                  NoConnectionException {
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
                Class proxyClass = this.onc.getClass();
                Method getMetaResource = proxyClass.getMethod(methodName, int.class);
                result = (AbstractMetaItem<?>) getMetaResource.invoke(this.onc, id);
                if (result != null) {
                    break;
                }
            } catch (InvocationTargetException e) {
                result = null;
                Throwable cause = e.getCause();

                /* HTTP 404 */
                if (cause != null && cause instanceof NotFoundException) {
                    Log.e("ResourceManager", "HTTP 404: meta '" + clazz + "' with id " + id, cause);
                    // TODO: implement better logging
                    break;
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws NoConnectionException {
        return fetchMetaItems(clazz, RestManager.DEFAULT_RETRY_COUNT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz, int retryCount) throws
                                                                                 NoConnectionException {
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
                Class proxyClass = this.onc.getClass();
                Method getMetaResources = proxyClass.getMethod(methodName);
                result = (List<AbstractMetaItem<?>>) getMetaResources.invoke(this.onc);
                if (result != null) {
                    break;
                }
            } catch (InvocationTargetException e) {
                result = null;
                Throwable cause = e.getCause();

                /* HTTP 404 */
                if (cause != null && cause instanceof NotFoundException) {
                    Log.i("ResourceManager", "HTTP 404: meta '" + clazz + "'", cause);
                    // TODO: implement better logging
                    break;
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
    }
}
