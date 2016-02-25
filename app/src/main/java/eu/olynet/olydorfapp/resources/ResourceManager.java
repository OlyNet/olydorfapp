package eu.olynet.olydorfapp.resources;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.ProcessingException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;

/**
 * The ResourceManager is the Singleton interface for accessing data on the OlyNet servers.
 * Requested data is cached using the DualCache library and provided as needed. The ResourceManager
 * has to be initialized with the application Context by calling its <i>init(Context)</i> method
 * prior to use. If this is neglected, a RuntimeException will be thrown.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class ResourceManager {

    /**
     * The static Map mapping the valid Classes to their corresponding identifier Strings.
     * <p/>
     * All items that need to be available via this Class have to be added in the static{...}
     * section below.
     *
     * @see eu.olynet.olydorfapp.resources.OlyNetClient
     */
    private static final Map<Class, String> treeCaches;

    /**
     * All items contained in this Set will be skipped during cleanup operations.
     */
    private static final Set<Class> skipDuringCleanup;

    /* statically fill the Map and the Set */
    static {
        Map<Class, String> initMap = new LinkedHashMap<>();
        initMap.put(NewsMetaItem.class, "news");
        initMap.put(FoodMetaItem.class, "food");
        initMap.put(DailyMealMetaItem.class, "motd");


        Set<Class> initSet = new LinkedHashSet<>();
        initSet.add(FoodMetaItem.class);

        treeCaches = Collections.unmodifiableMap(initMap);
        skipDuringCleanup = Collections.unmodifiableSet(initSet);
    }

    /**
     * The file containing the OlyNet e.V. custom Certificate Authority (CA).
     */
    private static final String CA_FILE = "olynet_ca.pem";

    /**
     * The file containing the OlyNet e.V. Intermediate Certificate Authority (CA).
     * <p/>
     * <b>IMPORTANT:</b> if this is not present, verification will fail when supplying a client
     * certificate. This seems to be a bug in Android's HttpsURLConnection implementation.
     */
    private static final String INTERMEDIATE_FILE = "olynet_intermediate.pem";

    /**
     * The file containing the version-specific user certificate for accessing the server.
     */
    private static final String CERTIFICATE_FILE = "app_01.pfx";

    /**
     * The decryption key for the version-specific user certificate.
     */
    private static final char[] CERTIFICATE_KEY = "$gf6yuW$%Cs4".toCharArray();

    /**
     * Singleton instance
     */
    private static ResourceManager ourInstance = new ResourceManager();

    private boolean initialized;
    private Context context;
    private OlyNetClient onc;

    private DualCache<TreeSet> metaTreeCache;
    private DualCache<AbstractMetaItem> itemCache;

    /**
     * @return the instance of the ResourceManager Singleton.
     */
    public static ResourceManager getInstance() {
        return ourInstance;
    }

    /**
     * Returns the StackTrace of an Exception as a String.
     *
     * @param e the Exception
     * @return the StackTrace as String.
     */
    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();

    }

    /**
     * Returns the String identifying an Object that can be handled by the ResourceManager.
     *
     * @param clazz the Class Object.
     * @return the identifying String.
     * @throws RuntimeException if clazz is not a valid Class for this operation.
     */
    private static String getResourceString(Class clazz) {
        String type = treeCaches.get(clazz);
        if (type == null || type.equals("")) {
            throw new RuntimeException("Class '" + clazz + "' is not a valid request Object");
        }

        return type;
    }

    /**
     * Empty constructor.
     */
    private ResourceManager() {
    }

    /**
     * Initializes the ResourceManager with the Context. This has to be called before any other
     * operation on the ResourceManager. Failure to do so will cause a RuntimeException. Duplicate
     * calls to this function are not recommended and will result in a warning.
     *
     * @param context the application Context.
     */
    public void init(Context context) {
        if (!initialized) {
            this.context = context.getApplicationContext();

            /* dynamically get the PackageInformation */
            PackageInfo pInfo;
            try {
                pInfo = this.context.getPackageManager().getPackageInfo(
                        this.context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return;
            }

            /* setup DualCache */
            DualCacheContextUtils.setContext(this.context);
            metaTreeCache = new DualCacheBuilder<>("MetaTrees", pInfo.versionCode, TreeSet.class)
                    .useDefaultSerializerInRam(5 * 1024 * 1024)
                    .useDefaultSerializerInDisk(10 * 1024 * 1024, true);
            itemCache = new DualCacheBuilder<>("Items", pInfo.versionCode, AbstractMetaItem.class)
                    .useDefaultSerializerInRam(5 * 1024 * 1024)
                    .useDefaultSerializerInDisk(200 * 1024 * 1024, true);
            Log.d("ResourceManager.init", "DualCache setup complete.");

            /*
             * setup ResteasyClient
             * ONLY MESS WITH THIS IF YOU KNOW EXACTLY WHAT YOU ARE DOING!
             */
            try {
                /* basic setup for certificate loading */
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                String algorithm = TrustManagerFactory.getDefaultAlgorithm();

                /*
                 * create a X509TrustManager that contains the OlyNet e.V. custom CA and the
                 * Intermediate CA. Both are necessary!
                 *
                 * See:
                 * https://stackoverflow.com/questions/27562666/programmatically-add-a-certificate-authority-while-keeping-android-system-ssl-ce
                 * https://stackoverflow.com/questions/35539969/https-with-client-authentication-not-working-on-android/35549515#35549515
                 */
                InputStream ca = this.context.getAssets().open(CA_FILE);
                InputStream intermediate = this.context.getAssets().open(INTERMEDIATE_FILE);
                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(null);
                Certificate caCert = cf.generateCertificate(ca);
                Certificate intermediateCert = cf.generateCertificate(intermediate);
                trustStore.setCertificateEntry("OlyNet e.V. Certificate Authority", caCert);
                trustStore.setCertificateEntry("OlyNet e.V. Intermediate Certificate Authority",
                        intermediateCert);
                CustomTrustManager tm = new CustomTrustManager(trustStore);
                ca.close();
                intermediate.close();

                /* create a KeyManagerFactory that contains our client certificate */
                InputStream clientCert = this.context.getAssets().open(CERTIFICATE_FILE);
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(clientCert, CERTIFICATE_KEY);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(keyStore, CERTIFICATE_KEY);
                clientCert.close();

                /* instantiate our SSLContext with the trust store and the key store*/
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);

                /* instantiate the HttpEngine we are going to use */
                HackedURLConnectionEngine engine = new HackedURLConnectionEngine();
                engine.setConnectionTimeout(5000);
                engine.setNotFuckedUpSslContext(sslContext);

                /* instantiate the ResteasyClient */
                ResteasyClient client = new ResteasyClientBuilder()
                        .providerFactory(new ResteasyProviderFactory()
                                .register(JacksonJsonProvider.class))
                        .httpEngine(engine)
                        .build();

                ResteasyWebTarget target = client.target("https://wstest.olynet.eu/dorfapp-rest/api");
                this.onc = target.proxy(OlyNetClient.class);

                Log.d("ResourceManager.init", "ResteasyClient setup complete.");
            } catch (Exception e) {
                throw new RuntimeException("ResourceManager initialization failed", e);
            }

            this.initialized = true;
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
    }

    /**
     * Checks whether the ResourceManager has been correctly initialized. Must be called at the
     * beginning of every public non-static function of this class.
     */
    private void checkInitialized() {
        if (!initialized)
            throw new RuntimeException("The ResourceManager has not been initialized. Initialize "
                    + "it by calling 'ResourceManager.getInstance().init(this); '"
                    + "in the MainActivity's onCreate()!");
    }

    /**
     * Displays a Toast to inform the user of something.
     *
     * @param msg the message to be displayed
     */
    private void informUser(final String msg) {
        Log.w("ResourceManager", msg);

        /* inform the user */
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Ensures that the device is connected to the internet.
     *
     * @throws NoConnectionException if no connection could be detected.
     */
    private void isOnline() throws NoConnectionException {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (!cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                throw new NoConnectionException("No internet connection detected");
            }
        } catch (Exception e) {
            throw new NoConnectionException("No internet connection detected", e);
        }
    }

    /**
     * @param type the String identifying a meta-data TreeSet in the cache.
     * @return the meta-data TreeSet (can be <b>null</b>)
     */
    @SuppressWarnings("unchecked")
    private TreeSet<AbstractMetaItem<?>> getCachedMetaDataTree(String type) {
        return metaTreeCache.get(type);
    }

    /**
     * Tries to fetch a specific item from the server.
     *
     * @param clazz the Class of the item to be fetched. Must be specified within the treeCaches
     *              Map.
     * @param id    the ID identifying the fetched item.
     * @return the fetched item or <b>null</b> if this operation was not successful.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws NoConnectionException if no internet connection is available.
     */
    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchItem(Class clazz, int id) throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        isOnline();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        AbstractMetaItem<?> result = null;
        try {
            Class proxyClass = this.onc.getClass();
            Method getResource = proxyClass.getMethod(methodName, int.class);
            result = (AbstractMetaItem) getResource.invoke(this.onc, id);
        } catch (InvocationTargetException e) {
            Log.w("ResourceManager", getStackTraceAsString(e));
            if (e.getCause() instanceof ProcessingException) {
                // TODO: inform the user of this somehow
                result = null;
            }
        } catch (Exception e) {
            Log.w("ResourceManager", getStackTraceAsString(e));
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Tries to fetch the up-to-date meta-data information from the server.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the fetched item or <b>null</b> if this operation was not successful.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws NoConnectionException if no internet connection is available.
     */
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        isOnline();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + "Meta" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchMetaItems", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result = new ArrayList<>();
        try {
            Class proxyClass = this.onc.getClass();
            Method getMetaResources = proxyClass.getMethod(methodName);
            result = (List<AbstractMetaItem<?>>) getMetaResources.invoke(this.onc);
        } catch (InvocationTargetException e) {
            Log.w("ResourceManager", getStackTraceAsString(e));
            if (e.getCause() instanceof ProcessingException) {
                // TODO: inform the user of this somehow
                result = null;
            }
        } catch (Exception e) {
            Log.w("ResourceManager", getStackTraceAsString(e));
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Performs a cleanup of all caches. Everything that has not been used in the last month will be
     * purged.
     *
     * @throws RuntimeException if the ResourceManager has not been initialized correctly.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {
        checkInitialized();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date cutoff = calendar.getTime();

        TreeSet<AbstractMetaItem<?>> cachedTree;
        Comparator<AbstractMetaItem> comparator = new AbstractMetaItem.LastUsedComparator();

        /* iterate over all entries in the static treeCaches Map */
        for (Map.Entry<Class, String> entry : treeCaches.entrySet()) {
            String type = entry.getValue();
            Class<?> clazz = entry.getKey();

            /* skip certain types during the cleanup */
            if(skipDuringCleanup.contains(clazz)) {
                Log.d("ResourceManager", "[cleanup] '" + type + "' skipped");
                continue;
            }

            Log.d("ResourceManager", "[cleanup] '" + type + "' started");
            try {
                /* get the TreeSet from the cache */
                cachedTree = metaTreeCache.get(type);
                if (cachedTree == null) {
                    Log.w("ResourceManager", "[cleanup] '" + type + "' failed, tree is null");
                    continue;
                }

                /* get a copy of the tree that is sorted by lastUsed */
                TreeSet<AbstractMetaItem> tree = new TreeSet<>(comparator);
                tree.addAll(cachedTree);

                /* dynamically get the dummy-constructor for the current type and create the filter */
                Constructor<?> cons = clazz.getConstructor(Date.class);
                AbstractMetaItem filterDummy = AbstractMetaItem.class.cast(cons.newInstance(cutoff));

                /* get all items last used on or before the cutoff date */
                Set<AbstractMetaItem> deleteSet = new HashSet<>(tree.headSet(
                        tree.floor(filterDummy), true));

                /* delete all cached entries of the full objects */
                for (AbstractMetaItem metaItem : deleteSet) {
                    itemCache.put(type + "_" + metaItem.getId(), null);
                }

                /* remove all references to deleted items from the meta-tree */
                tree.removeAll(deleteSet);

                /* write the pruned tree back to cache */
                metaTreeCache.put(type, tree);
            } catch (Exception e) {
                /* lord have mercy */
                throw new RuntimeException("cleanup of cached data failed for '" + type + "'", e);
            }
            Log.d("ResourceManager", "[cleanup] '" + type + "' finished");
        }
    }

    /**
     * Get a specific item from the server (preferably) or the cache.
     *
     * @param clazz the Class of the item to be fetched. Must be specified within the treeCaches
     *              Map.
     * @param id    the ID identifying the fetched item.
     * @return the requested item. This does not necessarily have to be up-to-date. If the server
     * cannot be reached in time, a cached version will be returned instead.
     * @throws RuntimeException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException if clazz is not a valid Class for this operation.
     */
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> getItem(Class<?> clazz, int id) {
        checkInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(type);
        if (tree == null) {
            Log.e("ResourceManager", "meta-data tree for " + clazz + " not found");
            return null;
        }

        try {
            /* create a dummy item with the same id */
            Constructor<?> cons = clazz.getConstructor(int.class);
            AbstractMetaItem<?> dummyItem = (AbstractMetaItem<?>) cons.newInstance(id);

            /* use the dummy item to search for the real item within the tree */
            AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
            if (metaItem == null || metaItem.getId() != id || metaItem.getLastUpdated() == null) {
                Log.e("ResourceManager", "meta-data tree " + clazz
                        + " does not contain the requested element " + id);
                return null;
            }

            /* check cache and query server on miss or date mismatch */
            String resIdentifier = treeCaches.get(clazz) + "_" + id;
            AbstractMetaItem<?> item = (AbstractMetaItem<?>) itemCache.get(resIdentifier);
            if (item == null || !item.getLastUpdated().equals(metaItem.getLastUpdated())) {
                Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                AbstractMetaItem<?> webItem = fetchItem(clazz, id);

                /* return webItem instead of the cached item if successful */
                if (webItem != null) {
                    item = webItem;
                } else {
                    Log.w("ResourceManager", "Fetch failed for some reason");
                }
            } else {
                Log.d("ResourceManager", "Cached item was up-to-date, no fetch necessary");
            }

            /* check if we have some valid item (up-to-date or not) */
            if (item != null) {
                /* update last used */
                item.setLastUsed();

                /* update meta-data tree */
                Method updateItem = clazz.getMethod("updateItem", clazz);
                updateItem.invoke(clazz.cast(metaItem), clazz.cast(item));

                /* write cache */
                Log.d("ResourceManager", "Updating cache for item '" + resIdentifier
                        + "' and meta-data tree '" + type + "'");
                itemCache.put(resIdentifier, item);
                metaTreeCache.put(type, tree);
            }

            return item;
        } catch (Exception e) {
            /* lord have mercy */
            Log.e("ResourceManager", getStackTraceAsString(e));
            return null;
        }
    }

    /**
     * Get meta-data of a specific category from the server (preferably) or the cache.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the requested meta-data. This does not necessarily have to be up-to-date. If the
     * server cannot be reached in time, a cached version will be returned instead.
     * @throws RuntimeException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException if clazz is not a valid Class for this operation.
     */
    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz) {
        checkInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(type);

        /* fetch meta-data from server */
        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>();
        List<AbstractMetaItem<?>> items = null;
        boolean noConnection = false;
        try {
            items = fetchMetaItems(clazz);
        } catch (NoConnectionException nce) {
            noConnection = true;
        }

        if (items != null) {
            Log.d("ResourceManager", "Received " + items.size() + " meta-data items from server");

            /* add all items to the result to be returned */
            result.addAll(items);

            /* insert and update all entries in cached metaDataTree */
            if (cachedTree == null) {
                cachedTree = new TreeSet<>();
            } else {
                cachedTree.removeAll(result);
            }
            cachedTree.addAll(result);

            /* write cache */
            metaTreeCache.put(type, cachedTree);
        } else {
            /* return cached data instead */
            if (noConnection) {
                informUser("No internet connection detected, using cached data instead.");
            } else {
                informUser("Unable to contact the server, using cached data instead.");
            }
            if (cachedTree == null) {
                Log.w("ResourceManager", "Cached metaTree is null");
            } else {
                Log.i("ResourceManager", "Cached metaTree size: " + cachedTree.size());
                result.addAll(cachedTree);
            }
        }

        return result;
    }

    /**
     * Not implemented yet.
     *
     * @param clazz      the Class of the item to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param first      the first element to be part of the returned selection.
     * @param last       the last element to be part of the returned selection.
     * @param comparator the Comparator used for ordering the meta-data tree.
     * @return the requested meta-data. This does not necessarily have to be up-to-date. If the
     * server cannot be reached in time, a cached version will be returned instead.
     * @throws RuntimeException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException if clazz is not a valid Class for this operation.
     */
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz, AbstractMetaItem<?> first,
                                                           AbstractMetaItem<?> last,
                                                           Comparator<AbstractMetaItem<?>> comparator) {
        checkInitialized();

        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>(comparator);

        // TODO: implement meta-data structure management and fetching from server

        return result;
    }
}
