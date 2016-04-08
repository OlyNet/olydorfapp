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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.NotFoundException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * The ResourceManager is the Singleton interface for accessing data on the OlyNet servers.
 * Requested data is cached using the DualCache library and provided as needed. The ResourceManager
 * has to be initialized with the application Context by calling its <i>init(Context)</i> method
 * prior to use. If this is neglected, a RuntimeException will be thrown.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ResourceManager {

    /**
     * The static Map mapping the valid Classes to their corresponding identifier Strings.
     * <p>
     * All items that need to be available via this Class have to be added in the static{...}
     * section below.
     *
     * @see eu.olynet.olydorfapp.resources.OlyNetClient
     * @see eu.olynet.olydorfapp.model.AbstractMetaItemMixIn
     */
    private static final Map<Class, String> treeCaches;

    /**
     * A static Map mapping the valid Classes to their corresponding locking Objects. Locking on the
     * Classes directly would also be possible, but could lead to problems if they were also used in
     * some other place.
     */
    private static final Map<Class, Object> lockMap;

    /**
     * All items contained in this Set will be skipped during cleanup operations.
     */
    private static final Set<Class> skipDuringCleanup;

    /* statically fill the Map and the Set */
    static {
        Map<Class, String> initTreeCaches = new LinkedHashMap<>();
        initTreeCaches.put(NewsMetaItem.class, "news");
        initTreeCaches.put(FoodMetaItem.class, "food");
        initTreeCaches.put(DailyMealMetaItem.class, "dailymeal");
        initTreeCaches.put(OrganizationMetaItem.class, "organization");
        initTreeCaches.put(MealOfTheDayMetaItem.class, "mealoftheday");

        Set<Class> initSkipDuringCleanup = new LinkedHashSet<>();
        initSkipDuringCleanup.add(FoodMetaItem.class);
        initSkipDuringCleanup.add(DailyMealMetaItem.class);
        initSkipDuringCleanup.add(OrganizationMetaItem.class);

        treeCaches = Collections.unmodifiableMap(initTreeCaches);
        skipDuringCleanup = Collections.unmodifiableSet(initSkipDuringCleanup);

        /* automatically generate lockMap */
        Map<Class, Object> initLockMap = new LinkedHashMap<>();
        for (Class<?> clazz : initTreeCaches.keySet()) {
            initLockMap.put(clazz, new Object());
        }
        lockMap = Collections.unmodifiableMap(initLockMap);
    }

    /**
     * Singleton instance
     */
    private static ResourceManager ourInstance = new ResourceManager();

    /**
     * Whether this ResourceManager has been properly initiated.
     */
    private boolean initialized;

    /**
     * The application Context.
     */
    private Context context;

    /**
     * The instanced ResteasyClient. Do not use directly.
     */
    private OlyNetClient onc;

    /**
     * The cache holding the meta-data trees. Do not use directly.
     */
    private DualCache<TreeSet> metaTreeCache;

    /**
     * The cache holding the full items. Do not use directly.
     */
    private DualCache<AbstractMetaItem> itemCache;

    /**
     * The cache holding the cacheLastUpdated Map while the app is not running.
     */
    private DualCache<Map> cacheStaleCache;

    /**
     * Contains the Dates the different metaTreeCaches have been last updated with information from
     * the server.
     */
    private Map<String, Date> cacheLastUpdated;

    /**
     * The time in minutes after which a cache entry is considered stale.
     */
    private static final int MINUTES_UNTIL_CACHE_STALE = 60;

    /**
     * The timeout in seconds after each message to the user.
     */
    private static final int TIMEOUT_AFTER_MESSAGE = 15;

    /**
     * The last time the user has been notified by calling informUser().
     */
    private Date lastNotifiedDate = null;

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
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private static String getResourceString(Class clazz) {
        String type = treeCaches.get(clazz);
        if (type == null || type.equals("")) {
            throw new IllegalArgumentException("Class '" + clazz
                    + "' is not a valid request Object");
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
    @SuppressWarnings("unchecked")
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
            cacheStaleCache =
                    new DualCacheBuilder<>("Stale", pInfo.versionCode, Map.class)
                            .useDefaultSerializerInRam(5 * 1024 * 1024)
                            .useDefaultSerializerInDisk(5 * 1024 * 1024, true);
            Log.d("ResourceManager.init", "DualCache setup complete.");

            /* get the lastUpdateCache from cache */
            cacheLastUpdated = cacheStaleCache.get("stale");
            if (cacheLastUpdated == null) {
                Log.w("ResourceManager.init", "cacheStaleCache was null");
                cacheLastUpdated = new HashMap<>();
            }

            /* debug the lastUpdateCache status */
            for (Map.Entry<String, Date> entry : cacheLastUpdated.entrySet()) {
                Log.d("ResourceManager.init", entry.getKey() + " - " + entry.getValue());
            }

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
                InputStream ca = this.context.getAssets().open(Configuration.CA_FILE);
                KeyStore trustStore = KeyStore.getInstance("PKCS12");
                trustStore.load(null);
                Certificate caCert = cf.generateCertificate(ca);
                trustStore.setCertificateEntry("OlyNet e.V. Certificate Authority", caCert);
                CustomTrustManager tm = new CustomTrustManager(trustStore);
                ca.close();

                /* create a KeyManagerFactory that contains our client certificate */
                InputStream clientCert = this.context.getAssets().open(
                        Configuration.CERTIFICATE_FILE);
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
                ResteasyClient client = new ResteasyClientBuilder()
                        .providerFactory(new ResteasyProviderFactory()
                                .register(JacksonJsonProvider.class))
                        .connectionPoolSize(4)
                        .connectionTTL(5, TimeUnit.MINUTES)
                        .httpEngine(engine)
                        .build();

                this.onc = client.target(Configuration.SERVER_BASE_URL).proxy(OlyNetClient.class);

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

        /* generate cutoff time */
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, -TIMEOUT_AFTER_MESSAGE);

        /* rate limiting */
        if (lastNotifiedDate == null || lastNotifiedDate.before(c.getTime())) {
            /* inform the user */
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });

            /* update the date */
            lastNotifiedDate = new Date();
        }
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
     * @param clazz the Class of the MetaItem.
     * @return the meta-data TreeSet (can be <b>null</b>)
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    @SuppressWarnings("unchecked")
    private TreeSet<AbstractMetaItem<?>> getCachedMetaDataTree(Class<?> clazz) {
        return metaTreeCache.get(getResourceString(clazz));
    }

    /**
     * Sets the cache entry for a specific MetaItem TreeSet.
     *
     * @param clazz the Class of the MetaItem.
     * @param tree  the TreeSet to write to cache. Setting this to <b>null</b> deletes the entry.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private void putCachedMetaDataTree(Class<?> clazz, @Nullable TreeSet<AbstractMetaItem<?>> tree) {
        metaTreeCache.put(getResourceString(clazz), tree);
    }

    /**
     * Sets the corresponding cacheLastUpdated Date to now.
     *
     * @param clazz the Class of the MetaItem.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private void setCacheLastUpdated(Class<?> clazz) {
        cacheLastUpdated.put(getResourceString(clazz), new Date());
        cacheStaleCache.put("stale", cacheLastUpdated);
    }

    /**
     * Check whether a meta-data TreeSet's cache entry is stale.
     *
     * @param clazz the Class of the MetaItem.
     * @return <b>true</b> if it is stale, <b>false</b> otherwise.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private boolean isCacheStale(Class<?> clazz) {
        Date cacheUpdateDate = cacheLastUpdated.get(getResourceString(clazz));
        if (cacheUpdateDate == null) {
            Log.w("ResourceManager", "cacheUpdateDate = null");
            return true;
        }

        /* get the latest possible Date where the cache is still considered not to be stale */
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -MINUTES_UNTIL_CACHE_STALE);
        Date staleDate = calendar.getTime();

        return staleDate.after(cacheUpdateDate);
    }

    /**
     * Returns a specific item from the cache.
     *
     * @param clazz the Class of the associated MetaItem
     * @param id    the ID identifying the item.
     * @return the requested item. Can be <b>null</b> if it is not present in the cache.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private AbstractMetaItem<?> getCachedItem(Class<?> clazz, int id) {
        return this.itemCache.get(getResourceString(clazz) + "_" + id);
    }

    /**
     * Updates the cache entry for a specific item.
     *
     * @param clazz the Class of the associated MetaItem
     * @param item  the item to put in the cache. Must not be <b>null</b>.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private void putCachedItem(Class<?> clazz, @NonNull AbstractMetaItem<?> item) {
        this.itemCache.put(getResourceString(clazz) + "_" + item.getId(), item);
    }

    /**
     * Deletes a cache entry for a specific item.
     *
     * @param clazz the Class of the associated MetaItem
     * @param id    the unique ID identifying the item.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    private void deleteCachedItem(Class<?> clazz, int id) {
        this.itemCache.put(getResourceString(clazz) + "_" + id, null);
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
    private AbstractMetaItem<?> fetchItem(Class clazz, int id) throws NoConnectionException {
        return fetchItem(clazz, id, 3);
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
    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchItem(Class clazz, int id, int retryCount)
            throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

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
                }
            } catch (Exception e) {
                Log.w("ResourceManager", "Exception during fetch - try " + i + "/" + retryCount, e);
                result = null;
            }
        }

        /* return the result that may still be null */
        return result;
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
    private List<AbstractMetaItem<?>> fetchItems(Class clazz) throws NoConnectionException {
        return fetchItems(clazz, 3);
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
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchItems(Class clazz, int retryCount)
            throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

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
    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchMetaItem(Class clazz, int id) throws NoConnectionException {
        return fetchMetaItem(clazz, id, 3);
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
    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchMetaItem(Class clazz, int id, int retryCount)
            throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

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
                    Log.e("ResourceManager", "HTTP 404: meta '" + clazz + "' with id " + id,
                            cause);
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
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws NoConnectionException {
        return fetchMetaItems(clazz, 3);
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
    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchMetaItems(Class clazz, int retryCount)
            throws NoConnectionException {
        /* terminate if we do not have an internet connection */
        verifyConnectivity();

        /* check if a valid type has been requested */
        String type = getResourceString(clazz);

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
        Comparator<AbstractMetaItem<?>> comparator = new AbstractMetaItem.LastUsedComparator();

        /* iterate over all entries in the static treeCaches Map */
        for (Map.Entry<Class, String> entry : treeCaches.entrySet()) {
            Class<?> clazz = entry.getKey();

            /* skip certain types during the cleanup */
            if (skipDuringCleanup.contains(clazz)) {
                Log.d("ResourceManager", "cleanup of '" + clazz + "' skipped");
                continue;
            }

            /* lock for thread-safety */
            synchronized (lockMap.get(clazz)) {
                Log.d("ResourceManager", "cleanup of '" + clazz + "' started");
                try {
                    /* get the TreeSet from the cache */
                    cachedTree = getCachedMetaDataTree(clazz);
                    if (cachedTree == null) {
                        Log.w("ResourceManager", "cleanup of '" + clazz + "' failed, tree is null");
                        continue;
                    }

                    /* get a copy of the tree that is sorted by lastUsedDate */
                    TreeSet<AbstractMetaItem<?>> tree = new TreeSet<>(comparator);
                    tree.addAll(cachedTree);

                    /* create a dummy element with the specific lastUsedDate */
                    AbstractMetaItem<?> filterDummy = new AbstractMetaItem.DummyFactory(clazz)
                            .setLastUsedDate(cutoff)
                            .build();

                    /* get all items last used on or before the cutoff createDate */
                    Set<AbstractMetaItem<?>> deleteSet = new HashSet<>(tree.headSet(
                            tree.floor(filterDummy), true));

                    /* delete all cached entries of the full objects */
                    for (AbstractMetaItem metaItem : deleteSet) {
                        deleteCachedItem(clazz, metaItem.getId());
                    }

                    /* remove all references to deleted items from the meta-tree */
                    tree.removeAll(deleteSet);

                    /* write the pruned tree back to cache */
                    putCachedMetaDataTree(clazz, tree);
                } catch (Exception e) {
                    throw new RuntimeException("cleanup of '" + clazz + "' failed", e);
                }
                Log.d("ResourceManager", "cleanup of '" + clazz + "' finished");
            }
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
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws RuntimeException         if the ID requested is not present in the meta-data tree or the
     *                                  whole tree itself is missing.
     * @throws RuntimeException         if some weird Reflection error occurs.
     */
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> getItem(Class<?> clazz, int id) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(clazz);
            if (tree == null) {
                throw new RuntimeException("meta-data tree for '" + clazz + "' not found");
            }

            try {
                /* create a dummy item with the same id */
                AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz)
                        .setId(id)
                        .build();

                /* use the dummy item to search for the real item within the tree */
                AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
                if (metaItem == null || metaItem.getId() != id || metaItem.getEditDate() == null) {
                    throw new RuntimeException("meta-data tree of type '" + clazz
                            + "' does not contain the requested element " + id);
                }

                /* check cache and query server on miss or createDate mismatch */
                AbstractMetaItem<?> item = getCachedItem(clazz, id);
                if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                    Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                    AbstractMetaItem<?> webItem;
                    try {
                        webItem = fetchItem(clazz, id);
                    } catch (NotFoundException e) { /* HTTP 404 */
                        webItem = null;

                        /* remove the item from the meta-data tree */
                        tree.remove(dummyItem);
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
                    Log.d("ResourceManager", "Cached item " + id + " of type '" + clazz +
                            "' was up-to-createDate, no fetch necessary");
                }

                /* check if we have some valid item (up-to-createDate or not) */
                if (item != null) {
                    /* update last used */
                    item.setLastUsedDate();

                    /* update meta-data tree */
                    Method updateItem = clazz.getMethod("updateItem", clazz);
                    updateItem.invoke(clazz.cast(metaItem), clazz.cast(item));

                    /* write item to cache */
                    Log.d("ResourceManager", "Updating cache for item with id " + id
                            + " and meta-data tree '" + clazz + "'");
                    putCachedItem(clazz, item);
                }

                /* write meta-data tree to cache */
                Log.d("ResourceManager", "Updating meta-data tree cache for '" + clazz + "'");
                putCachedMetaDataTree(clazz, tree);

                return item;
            } catch (Exception e) {
                Log.e("ResourceManager", "exception information if it gets wrapped to often", e);
                throw new RuntimeException("during the getItem for '" + clazz + "' id " + id, e);
            }
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
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws RuntimeException         if one of the IDs requested is not present in the meta-data
     *                                  tree or the whole tree itself is missing.
     * @throws RuntimeException         if some weird Reflection error occurs.
     */
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz, List<Integer> ids,
                                              Comparator<AbstractMetaItem<?>> comparator) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> tree = getCachedMetaDataTree(clazz);
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
                    AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz)
                            .setId(id)
                            .build();

                    /* use the dummy item to search for the real item within the tree */
                    AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
                    if (metaItem == null || metaItem.getId() != id
                            || metaItem.getEditDate() == null) {
                        throw new RuntimeException("meta-data tree of type '" + clazz
                                + "' does not contain the requested element " + id);
                    }

                    /* check cache and query server on miss or editDate mismatch */
                    AbstractMetaItem<?> item = getCachedItem(clazz, id);
                    if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                        Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                        AbstractMetaItem<?> webItem;
                        try {
                            webItem = fetchItem(clazz, id);
                        } catch (NotFoundException e) { /* HTTP 404 */
                            webItem = null;

                            /* remove the item from the meta-data tree */
                            tree.remove(dummyItem);
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
                        Log.d("ResourceManager", "Cached item " + id + " of type '" + clazz +
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
                        Log.d("ResourceManager", "Updating cache for item " + id + " of type " + clazz);
                        putCachedItem(clazz, item);

                        /* add item to the Collection of items to be returned */
                        itemTree.add(item);
                    }
                }
            } catch (Exception e) {
                Log.e("ResourceManager", "exception information in case it gets wrapped to often", e);
                throw new RuntimeException("during the getItems() for '" + clazz + "' with ids: "
                        + Arrays.toString(ids.toArray()), e);
            }

            Log.d("ResourceManager", "Updating meta-data tree cache of type '" + clazz + "'");
            putCachedMetaDataTree(clazz, tree);


            /* convert and return result */
            return new ArrayList<>(itemTree);
        }
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
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws RuntimeException         if some weird Reflection error occurs.
     */
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
        /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(clazz);
            TreeSet<AbstractMetaItem<?>> tree = new TreeSet<>();

            List<AbstractMetaItem<?>> result;
            try {
                result = fetchItems(clazz);
                if (result == null) {
                    throw new NoConnectionException(); /* dirty way to go to the catch clause */
                }

            /* dynamically create a MetaItem for each Item and add it to the new tree */
                for (AbstractMetaItem<?> item : result) {
                    Constructor<?> cons = clazz.getConstructor(clazz);
                    AbstractMetaItem<?> metaItem = (AbstractMetaItem<?>) cons.newInstance(item);
                    tree.add(metaItem);

                /* write item to cache */
                    Log.d("ResourceManager", "Updating cache for item " + item.getId() + " of type "
                            + clazz);
                    putCachedItem(clazz, item);
                }

            /* delete any items that no longer exist */
                if (cachedTree != null) {
                    cachedTree.removeAll(tree);
                    for (AbstractMetaItem<?> metaItem : cachedTree) {
                        Log.d("ResourceManager", "Deleting cached item " + metaItem.getId()
                                + " of type '" + clazz + "'");
                        deleteCachedItem(clazz, metaItem.getId());
                    }
                }

            /* write new meta-data tree to cache */
                Log.d("ResourceManager", "Updating meta-data tree cache of type '" + clazz + "'");
                putCachedMetaDataTree(clazz, tree);
                setCacheLastUpdated(clazz);
            } catch (NoConnectionException e) {
                if (cachedTree == null) {
                    result = null;
                } else {
                /* return List of cached items */
                    result = new ArrayList<>();
                    for (AbstractMetaItem<?> metaItem : cachedTree) {
                        AbstractMetaItem<?> item = getCachedItem(clazz, metaItem.getId());
                        if (item != null) {
                            result.add(item);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ResourceManager", "exception information in case it gets wrapped to often", e);
                throw new RuntimeException("during the getItems() for '" + clazz + "'", e);
            }

            return result;
        }
    }

    /**
     * Get meta-data of a specific category from the server or the cache.
     *
     * @param clazz       the Class of the meta-data to be fetched. Must be specified within the
     *                    treeCaches Map.
     * @param id          the id of the specific item that should be returned.
     * @param forceUpdate whether the meta-data should always fetched from the server.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws RuntimeException         if some weird Reflection error occured.
     * @deprecated Using this method usually does not make sense. Might be removed soon.
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> getMetaItem(Class<?> clazz, int id, boolean forceUpdate) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(clazz);

            /* fetch the up-to-date meta-data item from the server */
            AbstractMetaItem<?> result = null;
            if (isCacheStale(clazz) || forceUpdate || cachedTree == null) {
                try {
                    result = fetchMetaItem(clazz, id);
                } catch (NoConnectionException nce) {
                    result = null;
                }
                if (result == null) {
                    Log.w("ResourceManager", "fetch from server failed");
                }
            } else {
                Log.d("ResourceManager", "cache not stale, not querying server");
            }

            if (cachedTree == null) {
                Log.w("ResourceManager", "meta-data tree for '" + clazz + "' not found, creating it");
                cachedTree = new TreeSet<>();
            }

            /* try to use a cached item instead */
            if (result == null) {
                /* create a dummy item with the same id and use it to search for the requested item */
                AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz)
                        .setId(id)
                        .build();
                result = cachedTree.floor(dummyItem);

                /* if the returned item does not fit, set it to null */
                if (result == null || result.getId() != id || result.getEditDate() == null) {
                    result = null;
                }
            }

            /* update cache */
            if (result != null) {
                result.setLastUsedDate();
                cachedTree.remove(result);
                cachedTree.add(result);

                Log.d("ResourceManager", "Updating meta-data tree cache of type '" + clazz + "'");
                putCachedMetaDataTree(clazz, cachedTree);
            }

            /* return the result, which can still be null */
            return result;
        }
    }

    /**
     * Get meta-data of a specific category from the server or the cache.
     *
     * @param clazz       the Class of the meta-data to be fetched. Must be specified within the
     *                    treeCaches Map.
     * @param forceUpdate whether the meta-data should always fetched from the server.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, boolean forceUpdate) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(clazz);

            /* fetch meta-data from server */
            List<AbstractMetaItem<?>> items = null;
            boolean noConnection = false;
            boolean generalError = false;
            if (isCacheStale(clazz) || forceUpdate || cachedTree == null) {
                try {
                    items = fetchMetaItems(clazz);
                } catch (NoConnectionException nce) {
                    noConnection = true;
                }

                if (!noConnection && items == null) {
                    generalError = true;
                }
            } else {
                Log.d("ResourceManager", "cache not stale, not querying server");
            }

            TreeSet<AbstractMetaItem<?>> result = new TreeSet<>();
            if (items != null) {
                Log.d("ResourceManager", "Received " + items.size() + " meta-data items from server");

                /* add all items to the result to be returned */
                result.addAll(items);

                /* delete all no longer existing items */
                if (cachedTree != null) {
                    cachedTree.removeAll(result);
                    for (AbstractMetaItem<?> metaItem : cachedTree) {
                        Log.d("ResourceManager", "Deleting cached item " + metaItem.getId()
                                + " of type '" + clazz + "'");
                        deleteCachedItem(clazz, metaItem.getId());
                    }
                }

                /* write cache */
                putCachedMetaDataTree(clazz, result);
                setCacheLastUpdated(clazz);
            } else { /* return cached data instead */
                /* inform user of problem */
                if (noConnection) {
                    informUser("No internet connection detected, using cached data instead.");
                } else if (generalError) {
                    informUser("Unable to contact the server, using cached data instead.");
                }

                if (cachedTree == null) {
                    Log.w("ResourceManager", "Cached metaTree is null");
                    return null;
                } else {
                    Log.d("ResourceManager", "Cached metaTree size: " + cachedTree.size());
                    result.addAll(cachedTree);
                }
            }

            return result;
        }
    }

    /**
     * Returns a certain number of items of a specific type that are all greater (according to the
     * ordering) then a specified element.
     *
     * @param clazz       the Class of the item to be fetched. Must be specified within the
     *                    treeCaches Map.
     * @param after       exclusive lower bound. Can be null or a dummy.
     * @param limit       Number of elements that are returned. If it is below or equal to zero, no
     *                    limit is imposed.
     * @param comparator  the Comparator used for ordering the meta-data tree. Can be null for
     *                    default ordering.
     * @param forceUpdate whether the meta-data should always fetched from the server.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz,
                                                           int limit,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable Comparator<AbstractMetaItem<?>>
                                                                   comparator,
                                                           boolean forceUpdate) {
        abortIfNotInitialized();

        /*
         * no locking necessary - the actual work is delegated to getTreeOfMetaItems(clazz,
         * forceUpdate) which takes care of the locking
         */

        /* get the valid meta-data tree */
        TreeSet<AbstractMetaItem<?>> tempMetaTree = getTreeOfMetaItems(clazz, forceUpdate);
        if (tempMetaTree == null) {
            return null;
        }

        /* prepare the different TreeSets */
        TreeSet<AbstractMetaItem<?>> result;
        TreeSet<AbstractMetaItem<?>> preResult;
        TreeSet<AbstractMetaItem<?>> sortedMetaTree;
        if (comparator != null) {
            result = new TreeSet<>(comparator);
            preResult = new TreeSet<>(comparator);
            sortedMetaTree = new TreeSet<>(comparator);
        } else {
            result = new TreeSet<>();
            preResult = new TreeSet<>();
            sortedMetaTree = new TreeSet<>();
        }
        preResult.addAll(tempMetaTree);
        sortedMetaTree.addAll(tempMetaTree);

        /* remove everything before (and including) the 'after' AbstractMetaItem if it exists */
        if (after != null) {
            preResult.removeAll(sortedMetaTree.headSet(
                    sortedMetaTree.floor(after), true));
        }

        /* only take a specified number of items from the results */
        if (limit > 0) {
            int n = 0;
            for (AbstractMetaItem<?> item : preResult) {
                if (n++ >= limit) {
                    break;
                }
                result.add(item);
            }
        } else {
            result.addAll(preResult);
        }

        return result;
    }

    /**
     * Not implemented yet.
     *
     * @param clazz       the Class of the item to be fetched. Must be specified within the
     *                    treeCaches Map.
     * @param after       exclusive lower bound. Can be null or a dummy.
     * @param before      exclusive upper bound. Can be null or a dummy.
     * @param comparator  Can be null for
     *                    default ordering.
     * @param forceUpdate whether the meta-data should always fetched from the server.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable AbstractMetaItem<?> before,
                                                           @Nullable Comparator<AbstractMetaItem<?>>
                                                                   comparator,
                                                           boolean forceUpdate) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            TreeSet<AbstractMetaItem<?>> result = new TreeSet<>(comparator);

            // TODO: implement meta-data structure management and fetching from server

            return result;
        }
    }
}
