/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resources;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
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

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

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
        initMap.put(OrganizationMetaItem.class, "organization");

        Set<Class> initSet = new LinkedHashSet<>();
        initSet.add(FoodMetaItem.class);
        initSet.add(OrganizationMetaItem.class);

        treeCaches = Collections.unmodifiableMap(initMap);
        skipDuringCleanup = Collections.unmodifiableSet(initSet);
    }

    /**
     * The file containing the OlyNet e.V. custom Certificate Authority (CA).
     */
    private static final String CA_FILE = "olynet_ca.pem";

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
     * Returns the String identifying an Object that can be handled by the ResourceManager.
     *
     * @param clazz the Class Object.
     * @return the identifying String.
     * @throws RuntimeException if clazz is not a valid Class for this operation.
     */
    public static String getResourceString(Class clazz) {
        String type = treeCaches.get(clazz);
        if (type == null || type.equals("")) {
            throw new RuntimeException("Class '" + clazz + "' is not a valid request Object");
        }

        return type;
    }

    /**
     * Empty constructor. The real work is done by the init() method.
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
            DualCacheLogUtils.enableLog();
            DualCacheContextUtils.setContext(this.context);
            CacheSerializer<TreeSet> treeSerializer = new CacheSerializer<>(TreeSet.class);
            metaTreeCache = new DualCacheBuilder<>("MetaTrees", pInfo.versionCode, TreeSet.class)
                    .useCustomSerializerInRam(5 * 1024 * 1024, treeSerializer)
                    .useCustomSerializerInDisk(10 * 1024 * 1024, true, treeSerializer);
            CacheSerializer<AbstractMetaItem> itemSerializer =
                    new CacheSerializer<>(AbstractMetaItem.class);
            itemCache = new DualCacheBuilder<>("Items", pInfo.versionCode, AbstractMetaItem.class)
                    .useCustomSerializerInRam(5 * 1024 * 1024, itemSerializer)
                    .useCustomSerializerInDisk(200 * 1024 * 1024, true, itemSerializer);
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
                 * create a X509TrustManager that contains the OlyNet e.V. custom CA.
                 *
                 * See:
                 * https://stackoverflow.com/questions/27562666/programmatically-add-a-certificate-authority-while-keeping-android-system-ssl-ce
                 */
                InputStream ca = this.context.getAssets().open(CA_FILE);
                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(null);
                Certificate caCert = cf.generateCertificate(ca);
                trustStore.setCertificateEntry("OlyNet e.V. Certificate Authority", caCert);
                CustomTrustManager tm = new CustomTrustManager(trustStore);
                ca.close();

                /* create a KeyManagerFactory that contains our client certificate */
                InputStream clientCert = this.context.getAssets().open(CERTIFICATE_FILE);
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(clientCert, CERTIFICATE_KEY);
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
                kmf.init(keyStore, CERTIFICATE_KEY);
                clientCert.close();

                /* instantiate our SSLContext with the trust store and the key store */
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
                throw new RuntimeException("ResteasyClient setup failed", e);
            }

            this.initialized = true;
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
    }

    /**
     * Has the ResourceManager been properly intialized?
     *
     * @return <b>true</b> if and only if the ResourceManager has been properly initialized.
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Checks whether the ResourceManager has been correctly initialized. Must be called at the
     * beginning of every public non-static function of this class.
     *
     * @throws IllegalStateException if this is not the case.
     */
    private void abortIfNotInitialized() {
        if (!initialized)
            throw new IllegalStateException("The ResourceManager has not been initialized." +
                    "Initialize it by calling 'ResourceManager.getInstance().init(this); '"
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
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ensures that the device is connected to the internet.
     *
     * @throws NoConnectionException if no connection could be detected.
     */
    private void verifyConnectivity() throws NoConnectionException {
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
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        AbstractMetaItem<?> result;
        try {
            Class proxyClass = this.onc.getClass();
            Method getResource = proxyClass.getMethod(methodName, int.class);
            result = (AbstractMetaItem) getResource.invoke(this.onc, id);
        } catch (Exception e) {
            Log.w("ResourceManager", "Exception during fetch", e);
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Tries to fetch all items of a specific type from the server.
     *
     * @param clazz the Class of the items to be fetched. Must be specified within the treeCaches
     *              Map.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws NoConnectionException if no internet connection is available.
     */
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchItems(Class clazz) throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchItem", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result;
        try {
            Class proxyClass = this.onc.getClass();
            Method getResource = proxyClass.getMethod(methodName);
            result = (List<AbstractMetaItem<?>>) getResource.invoke(this.onc);
        } catch (Exception e) {
            Log.w("ResourceManager", "Exception during fetch", e);
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information for one specific item from the
     * server.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @param id    the id of the item for which the meta-data is to be fetched.
     * @return the meta-data item or <b>null</b> if this operation failed.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws NoConnectionException if no internet connection is available.
     */
    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchMetaItem(Class clazz, int id) throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + "Meta" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchMetaItem", methodName);

        /* dynamically invoke the correct Method */
        AbstractMetaItem<?> result;
        try {
            Class proxyClass = this.onc.getClass();
            Method getMetaResource = proxyClass.getMethod(methodName, int.class);
            result = (AbstractMetaItem<?>) getMetaResource.invoke(this.onc, id);
        } catch (Exception e) {
            Log.w("ResourceManager", "Exception during fetch", e);
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Tries to fetch the up-to-createDate meta-data information from the server.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the fetched List of meta-data items or <b>null</b> if this operation failed.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws NoConnectionException if no internet connection is available.
     */
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

        /* generate function name from type String */
        String methodName = "get" + "Meta" + type.substring(0, 1).toUpperCase() + type.substring(1);
        Log.d("fetchMetaItems", methodName);

        /* dynamically invoke the correct Method */
        List<AbstractMetaItem<?>> result;
        try {
            Class proxyClass = this.onc.getClass();
            Method getMetaResources = proxyClass.getMethod(methodName);
            result = (List<AbstractMetaItem<?>>) getMetaResources.invoke(this.onc);
        } catch (Exception e) {
            Log.w("ResourceManager", "Exception during fetch", e);
            result = null;
        }

        /* return the result that may still be null */
        return result;
    }

    /**
     * Performs a cleanup of all caches. Everything that has not been used in the last month will be
     * purged.
     *
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {
        abortIfNotInitialized();

        /* create a Date object exactly one month in the past */
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
            if (skipDuringCleanup.contains(clazz)) {
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

                /* get a copy of the tree that is sorted by lastUsedDate */
                TreeSet<AbstractMetaItem> tree = new TreeSet<>(comparator);
                tree.addAll(cachedTree);

                /* dynamically get the dummy-constructor for the current type and create the filter */
                Constructor<?> cons = clazz.getConstructor(Date.class);
                AbstractMetaItem filterDummy = AbstractMetaItem.class.cast(cons.newInstance(cutoff));

                /* get all items last used on or before the cutoff createDate */
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
     * @return the requested item. This does not necessarily have to be up-to-createDate. If the
     * server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws RuntimeException      if the ID requested is not present in the meta-data tree or the
     *                               whole tree itself is missing.
     * @throws RuntimeException      if some weird Reflection error occurs.
     */
    public AbstractMetaItem<?> getItem(Class<?> clazz, int id) {
        abortIfNotInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(type);
        if (tree == null) {
            throw new RuntimeException("meta-data tree for '" + clazz + "' not found");
        }

        try {
            /* create a dummy item with the same id */
            Constructor<?> cons = clazz.getConstructor(int.class);
            AbstractMetaItem<?> dummyItem = (AbstractMetaItem<?>) cons.newInstance(id);

            /* use the dummy item to search for the real item within the tree */
            AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
            if (metaItem == null || metaItem.getId() != id || metaItem.getEditDate() == null) {
                throw new RuntimeException("meta-data tree of type '" + type
                        + "' does not contain the requested element " + id);
            }

            /* check cache and query server on miss or createDate mismatch */
            String resIdentifier = treeCaches.get(clazz) + "_" + id;
            AbstractMetaItem<?> item = (AbstractMetaItem<?>) itemCache.get(resIdentifier);
            if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                AbstractMetaItem<?> webItem;
                try {
                    webItem = fetchItem(clazz, id);
                } catch (NoConnectionException e) {
                    webItem = null;
                }

                /* return webItem instead of the cached item if successful */
                if (webItem != null) {
                    item = webItem;
                } else {
                    Log.w("ResourceManager", "Fetch failed for some reason");
                }
            } else {
                Log.d("ResourceManager", "Cached item " + id + " of type '" + type +
                        "' was up-to-createDate, no fetch necessary");
            }

            /* check if we have some valid item (up-to-createDate or not) */
            if (item != null) {
                /* update last used */
                item.setLastUsedDate();

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
            throw new RuntimeException("during the getItem() for '" + type + "' with id: " + id, e);
        }
    }

    /**
     * Get a list of items from the server (preferably) or the cache.
     *
     * @param clazz      the Class of the item to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param ids        the List of IDs identifying the fetched items.
     * @param comparator The comparator specifying the ordering of the items. If it is <b>null</b>,
     *                   items will be sorted using their inherent order.
     * @return a List of the requested Items with the specified ordering.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws RuntimeException      if one of the IDs requested is not present in the meta-data
     *                               tree or the whole tree itself is missing.
     * @throws RuntimeException      if some weird Reflection error occurs.
     */
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz, List<Integer> ids,
                                              Comparator<AbstractMetaItem> comparator) {
        abortIfNotInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(type);
        if (tree == null) {
            throw new RuntimeException("meta-data tree for '" + clazz + "' not found, creating it");
        }

        /* TreeSet used for ordering */
        TreeSet<AbstractMetaItem<?>> itemTree;
        if (comparator != null) {
            itemTree = new TreeSet<>(comparator);
        } else {
            itemTree = new TreeSet<>();
        }

        try {
            for (int id : ids) {
                /* create a dummy item with the same id */
                Constructor<?> cons = clazz.getConstructor(int.class);
                AbstractMetaItem<?> dummyItem = (AbstractMetaItem<?>) cons.newInstance(id);

                /* use the dummy item to search for the real item within the tree */
                AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
                if (metaItem == null || metaItem.getId() != id || metaItem.getEditDate() == null) {
                    throw new RuntimeException("meta-data tree of type '" + type
                            + "' does not contain the requested element " + id);
                }

                /* check cache and query server on miss or createDate mismatch */
                String resIdentifier = treeCaches.get(clazz) + "_" + id;
                AbstractMetaItem<?> item = (AbstractMetaItem<?>) itemCache.get(resIdentifier);
                if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                    Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                    AbstractMetaItem<?> webItem;
                    try {
                        webItem = fetchItem(clazz, id);
                    } catch (NoConnectionException e) {
                        webItem = null;
                    }

                    /* return webItem instead of the cached item if successful */
                    if (webItem != null) {
                        item = webItem;
                    } else {
                        Log.w("ResourceManager", "Fetch failed for some reason");
                    }
                } else {
                    Log.d("ResourceManager", "Cached item " + id + " of type '" + type +
                            "' was up-to-createDate, no fetch necessary");
                }

                /* check if we have some valid item (up-to-createDate or not) */
                if (item != null) {
                    /* update last used */
                    item.setLastUsedDate();

                    /* update meta-data tree */
                    Method updateItem = clazz.getMethod("updateItem", clazz);
                    updateItem.invoke(clazz.cast(metaItem), clazz.cast(item));

                    /* write updated item to cache */
                    Log.d("ResourceManager", "Updating cache for item '" + resIdentifier + "'");
                    itemCache.put(resIdentifier, item);

                    /* add item to the Collection of items to be returned */
                    itemTree.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("during the getItems() for '" + type + "' with ids: "
                    + Arrays.toString(ids.toArray()), e);
        }

        Log.d("ResourceManager", "Updating meta-data tree cache of type '" + type + "'");
        metaTreeCache.put(type, tree);

        /* convert and return result */
        return new ArrayList<>(itemTree);
    }

    /**
     * Get a list containing all items of a type from the server. The cache is only used as a
     * fallback if the server is not reachable or replying with an invalid response. <b>Using
     * this method will therefore result in a lot of network traffic, as ALL available information
     * of a specific type will be pulled from the server. Use only when necessary!</b>
     *
     * @param clazz the Class of the item to be fetched. Must be specified within the treeCaches
     *              Map.
     * @return a List of the requested Items.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws RuntimeException      if some weird Reflection error occurs.
     */
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz) {
        abortIfNotInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(type);
        TreeSet<AbstractMetaItem<?>> tree = new TreeSet<>();

        List<AbstractMetaItem<?>> result;
        try {
            result = fetchItems(clazz);
            if (result == null) {
                throw new NoConnectionException(); /* dirty way to go to the catch clause */
            }

            /* dynamically create a MetaItem for each Item and add it to the new tree */
            for (AbstractMetaItem<?> item : result) {
                String resIdentifier = type + "_" + item.getId();
                Constructor<?> cons = clazz.getConstructor(clazz);
                AbstractMetaItem<?> metaItem = (AbstractMetaItem<?>) cons.newInstance(item);
                tree.add(metaItem);

                /* write item to cache */
                Log.d("ResourceManager", "Updating cache for item '" + resIdentifier + "'");
                itemCache.put(resIdentifier, item);
            }

            /* delete any items that no longer exist */
            if (cachedTree != null) {
                cachedTree.removeAll(tree);
                for (AbstractMetaItem<?> metaItem : cachedTree) {
                    String resIdentifier = type + "_" + metaItem.getId();
                    Log.d("ResourceManager", "Deleting cached item '" + resIdentifier + "'");
                    itemCache.put(resIdentifier, null);
                }
            }

            /* write new meta-data tree to cache */
            Log.d("ResourceManager", "Updating meta-data tree cache of type '" + type + "'");
            metaTreeCache.put(type, tree);
        } catch (NoConnectionException e) {
            if (cachedTree == null) {
                result = null;
            } else {
                /* return List of cached items */
                result = new ArrayList<>();
                for (AbstractMetaItem<?> metaItem : cachedTree) {
                    String resIdentifier = type + "_" + metaItem.getId();
                    AbstractMetaItem<?> item = itemCache.get(resIdentifier);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("during the getItems() for '" + type + "'", e);
        }

        return result;
    }

    /**
     * Get meta-data of a specific category from the server (preferably) or the cache.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     * @throws RuntimeException      if some weird Reflection error occured.
     */
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> getMetaItem(Class clazz, int id) {
        abortIfNotInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(type);
        if (tree == null) {
            Log.w("ResourceManager", "meta-data tree for '" + clazz + "' not found, creating it");
            tree = new TreeSet<>();
        }

        for (AbstractMetaItem<?> item : tree) {
            Log.w("before", item.toString());
        }

        /* fetch the up-to-date meta-data item from the server */
        AbstractMetaItem<?> result;
        try {
            result = fetchMetaItem(clazz, id);
        } catch (NoConnectionException nce) {
            result = null;
        }

        /* try to use a cached item instead */
        if (result == null) {
            Log.w("ResourceManager", "fetch from server failed, falling back to cache");

            /* create a dummy item with the same id */
            AbstractMetaItem<?> dummyItem;
            try {
                Constructor<?> cons = clazz.getConstructor(int.class);
                dummyItem = (AbstractMetaItem<?>) cons.newInstance(id);
            } catch (Exception e) {
                throw new RuntimeException("dynamic constructor invocation failed", e);
            }
            result = tree.floor(dummyItem);

            /* if the returned item does not fit, set it to null */
            if (result == null || result.getId() != id || result.getEditDate() == null) {
                result = null;
            }
        }

        /* update cache */
        if (result != null) {
            result.setLastUsedDate();
            tree.remove(result);
            tree.add(result);

            Log.d("ResourceManager", "Updating meta-data tree cache of type '" + type + "' with " + id);
            metaTreeCache.put(type, tree);
        }

        /* return the result, which can still be null */
        return result;
    }

    /**
     * Get meta-data of a specific category from the server (preferably) or the cache.
     *
     * @param clazz the Class of the meta-data to be fetched. Must be specified within the
     *              treeCaches Map.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     */
    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz) {
        abortIfNotInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(type);

        /* fetch meta-data from server */
        List<AbstractMetaItem<?>> items = null;
        boolean noConnection = false;
        try {
            items = fetchMetaItems(clazz);
        } catch (NoConnectionException nce) {
            noConnection = true;
        }

        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>();
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
                return null;
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
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     * @throws RuntimeException      if clazz is not a valid Class for this operation.
     */
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz, AbstractMetaItem<?> first,
                                                           AbstractMetaItem<?> last,
                                                           Comparator<AbstractMetaItem<?>> comparator) {
        abortIfNotInitialized();

        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>(comparator);

        // TODO: implement meta-data structure management and fetching from server

        return result;
    }
}
