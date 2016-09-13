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
 * http://stackoverflow.com/q/27562666/3997552
 * http://nelenkov.blogspot.de/2011/12/using-custom-certificate-trust-store-on.html
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
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
    }

    private X509TrustManager createTrustManager(KeyStore store) throws NoSuchAlgorithmException,
                                                                       KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(store);
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
     * @throws CertificateException     if the certificate chain can't be validated or isn't
     *                                  trusted.
     * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
     *                                  or if the specified authentication type is {@code null} or
     *                                  an empty string.
     */
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                                                                             CertificateException {
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
     * @throws CertificateException     if the certificate chain can't be validated or isn't
     *                                  trusted.
     * @throws IllegalArgumentException if the specified certificate chain is empty or {@code null},
     *                                  or if the specified authentication type is {@code null} or
     *                                  an empty string.
     */
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                                                                             CertificateException {
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
