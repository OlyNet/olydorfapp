/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resources;

import android.util.Log;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Implementation of a custom X509TrustManager that not only trusts the system-default Certificate
 * Authorities but also the ones provided by a custom trust store. It works by first checking the
 * custom trust store and falling back to the default trust manager if that fails.
 * <p/>
 * Sources:
 * https://stackoverflow.com/questions/27562666/programmatically-add-a-certificate-authority-while-keeping-android-system-ssl-ce
 * http://nelenkov.blogspot.de/2011/12/using-custom-certificate-trust-store-on.html
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class CustomTrustManager implements X509TrustManager {

    private X509TrustManager defaultTrustManager;
    private X509TrustManager localTrustManager;

    public CustomTrustManager(KeyStore customTrustStore) {
        try {
            this.defaultTrustManager = createTrustManager(null);
            this.localTrustManager = createTrustManager(customTrustStore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("CustomTrustManager", "Initialization successful.");
    }

    private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException,
            KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) store);
        TrustManager[] trustManagers = tmf.getTrustManagers();
        return (X509TrustManager) trustManagers[0];
    }

    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for client authentication for the specified
     * authentication type.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the authentication type used.
     * @throws CertificateException     if the certificate chain can't be validated or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
     *                                  or if the specified authentication type is {@code null} or an
     *                                  empty string.
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            localTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException ce) {
            defaultTrustManager.checkClientTrusted(chain, authType);
        }
    }

    /**
     * Checks whether the specified certificate chain (partial or complete) can
     * be validated and is trusted for server authentication for the specified
     * key exchange algorithm.
     *
     * @param chain    the certificate chain to validate.
     * @param authType the key exchange algorithm name.
     * @throws CertificateException     if the certificate chain can't be validated or isn't trusted.
     * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
     *                                  or if the specified authentication type is {@code null} or an
     *                                  empty string.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            localTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException ce) {
            defaultTrustManager.checkServerTrusted(chain, authType);
        }
    }

    /**
     * Returns the list of certificate issuer authorities which are trusted for
     * authentication of peers.
     *
     * @return the list of certificate issuer authorities which are trusted for
     * authentication of peers.
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] localCerts = localTrustManager.getAcceptedIssuers();
        X509Certificate[] defaultCerts = defaultTrustManager.getAcceptedIssuers();
        X509Certificate[] result = Arrays.copyOf(localCerts,
                localCerts.length + defaultCerts.length);
        System.arraycopy(defaultCerts, 0, result, localCerts.length, defaultCerts.length);
        return result;
    }
}
