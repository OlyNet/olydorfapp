package eu.olynet.olydorfapp.resources;

import android.content.Context;
import android.util.Log;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.io.InputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class ResourceManager {

    private final static String CA_FILE = "olynet_ca.crt";
    private final static String CERTIFICATE_FILE = "client_certificate.p12";
    private final static char[] CERTIFICATE_KEY = "1234567".toCharArray();

    private static ResourceManager ourInstance = new ResourceManager();

    private boolean initialized;
    private Context context;
    private ResteasyClient client;

    public static ResourceManager getInstance() {
        return ourInstance;
    }

    private ResourceManager() {
    }

    private void checkInitialized() {
        if (!initialized)
            throw new RuntimeException("The ResourceManager has not been initialized. Initialize "
                    + "it by calling 'ResourceManager.getInstance().init(this); '"
                    + "in the MainActivity's onCreate()!");
    }

    public void init(Context context) {
        if (!initialized) {
            this.context = context.getApplicationContext();

            /* setup DualCache */
            DualCacheContextUtils.setContext(this.context);
            DualCacheLogUtils.enableLog();
            Log.i("ResourceManager.init", "DualCache setup complete.");

            /* setup ResteasyClient */
            try {
                /* create a KeyStore that contains our client certificate */
                //InputStream certificate = context.getAssets().open(CERTIFICATE_FILE);
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null);
                //keyStore.load(certificate, CERTIFICATE_KEY);

                ClientHttpEngine engine = new URLConnectionEngine();
                client = new ResteasyClientBuilder().httpEngine(engine)
                        .keyStore(keyStore, new char[0])
                        .build();
                Log.i("ResourceManager.init", "ResteasyClient setup complete.");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } catch (KeyStoreException e) {
                e.printStackTrace();
                return;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            } catch (CertificateException e) {
                e.printStackTrace();
                return;
            }


            this.initialized = true;
        } else {
            Log.w("ResourceManager.init", "Duplicate init");
        }
    }

    public void cleanup() {
        checkInitialized();
    }

    public AbstractMetaItem<?> getSingleItem(Class clazz, long id) {
        checkInitialized();

        return null;
    }

    public List<AbstractMetaItem<?>> getListOfMetaItems(Class clazz, AbstractMetaItem<?> first,
                                                        AbstractMetaItem<?> last,
                                                        Comparator<? extends AbstractMetaItem<?>> comparator) {
        checkInitialized();

        return null;
    }
}


interface OlyNetClient {

}
