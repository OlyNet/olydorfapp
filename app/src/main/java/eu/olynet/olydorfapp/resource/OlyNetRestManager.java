package eu.olynet.olydorfapp.resource;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class OlyNetRestManager extends RestManager {

    private SSLContext sslContext = null;

    public OlyNetRestManager(Context context) {
        super(context);
    }

    /**
     * Initializes this RestManager. Is called by the constructor and must be used to configure
     * each implementation of this abstract class.
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
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);

        } catch (Exception e) {
            throw new RuntimeException("REST client setup failed", e);
        }
    }

    private RestResponse get(URL url, Map<String, String> requestProperties) {
        RestResponse response = new RestResponse();
        HttpsURLConnection urlConnection = null;
        try {
            /* setup the connection */
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(this.sslContext.getSocketFactory());
            urlConnection.setRequestMethod("GET");

            /* set request properties */
            for (Map.Entry<String, String> property : requestProperties.entrySet()) {
                urlConnection.setRequestProperty(property.getKey(), property.getValue());
            }

            /* get response code */
            response.setResponseCode(urlConnection.getResponseCode());

            /* get headers */
            response.setResponseHeaders(urlConnection.getHeaderFields());

            /*
             * get content type and charset
             * http://stackoverflow.com/a/3934280/3997552
             */
            response.setContentType(urlConnection.getContentType());
            String[] values = response.getContentType().split(";");
            String charset = "";
            for (String value : values) {
                value = value.trim();

                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                }
            }
            if ("".equals(charset)) {
                charset = "UTF-8";  /* assumption */
            }

            /*
             * get the content
             * http://stackoverflow.com/a/309448/3997552
             */
            StringWriter writer = new StringWriter();
            IOUtils.copy(urlConnection.getInputStream(), writer, charset);
            response.setResponseMessage(writer.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            /* disconnect so the connection can be reused */
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return response;
    }

    /**
     * Tries to fetch a specific item from the server. Defaults to 3 retries.
     *
     * @param clazz the Class of the item to be fetched. Must be specified within the treeCaches
     *              Map.
     * @param id    the ID identifying the fetched item.
     * @return the fetched item or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     * @throws NotFoundException        if a HTTP 404 has been received.
     */
    @Override
    public AbstractMetaItem<?> fetchItem(Class clazz, int id) throws NoConnectionException {
        return fetchItem(clazz, id, DEFAULT_RETRY_COUNT);
    }

    /**
     * Tries to fetch a specific item from the server.
     *
     * @param clazz      the Class of the item to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param id         the ID identifying the fetched item.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched item or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     * @throws NotFoundException        if a HTTP 404 has been received.
     */
    @Override
    public AbstractMetaItem<?> fetchItem(Class clazz, int id, int retryCount) throws
                                                                              NoConnectionException {
        return null;
    }

    /**
     * Tries to fetch all items of a specific type from the server. Defaults to 3 retries.
     *
     * @param clazz the Class of the items to be fetched. Must be specified within the treeCaches
     *              Map.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz) throws NoConnectionException {
        return fetchItems(clazz, DEFAULT_RETRY_COUNT);
    }

    /**
     * Tries to fetch all items of a specific type from the server.
     *
     * @param clazz      the Class of the items to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, int retryCount) throws
                                                                             NoConnectionException {
        return null;
    }

    /**
     * Tries to fetch all items of a specific type from the server. Defaults to 3 retries.
     *
     * @param clazz the Class of the items to be fetched. Must be specified within the treeCaches
     *              Map.
     * @param ids   the List containing the ids to fetch from the server. Must not contain any
     *              <b>null</b> elements.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids)
            throws NoConnectionException {
        return fetchItems(clazz, ids, DEFAULT_RETRY_COUNT);
    }

    /**
     * Tries to fetch all items of a specific type from the server.
     *
     * @param clazz      the Class of the items to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param ids        the List containing the ids to fetch from the server. Must not contain any
     *                   <b>null</b> elements.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids, int retryCount)
            throws NoConnectionException {
        return null;
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information for one specific item from the
     * server. Defaults to 3 retries.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @param id    the id of the item for which the meta-data is to be fetched.
     * @return the meta-data item or <b>null</b> if this operation failed.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id) throws NoConnectionException {
        return fetchMetaItem(clazz, id, DEFAULT_RETRY_COUNT);
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information for one specific item from the
     * server.
     *
     * @param clazz      the Class of the meta-data to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param id         the id of the item for which the meta-data is to be fetched.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the meta-data item or <b>null</b> if this operation failed.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public AbstractMetaItem<?> fetchMetaItem(Class clazz, int id, int retryCount) throws
                                                                                  NoConnectionException {
        return null;
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information from the server. Defaults to 3
     * retries.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the fetched List of meta-data items or <b>null</b> if this operation failed.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws NoConnectionException {
        return fetchMetaItems(clazz, DEFAULT_RETRY_COUNT);
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information from the server.
     *
     * @param clazz      the Class of the meta-data to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched List of meta-data items or <b>null</b> if this operation failed.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    @Override
    public List<AbstractMetaItem<?>> fetchMetaItems(Class clazz, int retryCount) throws
                                                                                 NoConnectionException {
        return null;
    }
}
