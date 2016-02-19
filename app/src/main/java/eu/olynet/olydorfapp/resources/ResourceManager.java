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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class ResourceManager {

    private static final Map<Class, String> treeCaches;

    static {
        Map<Class, String> initMap = new LinkedHashMap<>();
        initMap.put(NewsMetaItem.class, "news");

        treeCaches = Collections.unmodifiableMap(initMap);
    }

    private static final String CA_FILE = "olynet_ca.crt";
    private static final String CERTIFICATE_FILE = "client_certificate.p12";
    private static final char[] CERTIFICATE_KEY = "1234567".toCharArray();

    private static ResourceManager ourInstance = new ResourceManager();

    private boolean initialized;
    private Context context;
    private OlyNetClient onc;

    private DualCache<TreeSet> metaTreeCache;
    private DualCache<AbstractMetaItem> itemCache;

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

    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public void init(Context context) {
        if (!initialized) {
            this.context = context.getApplicationContext();

            /* dynamically get the PackageInformation */
            PackageInfo pInfo;
            try {
                pInfo = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
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
                    .useDefaultSerializerInDisk(50 * 1024 * 1024, true);
            Log.d("ResourceManager.init", "DualCache setup complete.");

            /*
             * setup ResteasyClient
             * ONLY MESS WITH THIS IF YOU KNOW EXACTLY WHAT YOU ARE DOING!
             */
            try {
                /* create a KeyStore that contains our client certificate */
                //InputStream certificate = this.context.getAssets().open(CERTIFICATE_FILE);
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null);
                //keyStore.load(certificate, CERTIFICATE_KEY);


                /* instantiate the HttpEngine we are going to use */
                HackedURLConnectionEngine engine = new HackedURLConnectionEngine();
                engine.setConnectionTimeout(5000);

                /* instantiate the ResteasyClient */
                ResteasyClient client = new ResteasyClientBuilder()
                        .httpEngine(engine)
                        .keyStore(keyStore, new char[0])
                        .build();

                client.register(JacksonJsonProvider.class);

                ResteasyWebTarget target = client.target("http://web1.olydorf.mhn.de:8230/dorfapp-rest/api");
                onc = target.proxy(OlyNetClient.class);

                Log.d("ResourceManager.init", "ResteasyClient setup complete.");
            } catch (Exception e) {
                Log.e("ResourceManager.init", getStackTraceAsString(e));
                return;
            }


            this.initialized = true;
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
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
     * @return <b>true</b> if there is a connection to the internet available, <b>false</b> otherwise.
     */
    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
                return true;
            } else {
                informUser("No internet connection detected.");
                return false;
            }
        } catch (Exception e) {
            informUser("No internet connection detected.");
            return false;
        }
    }

    /**
     * Returns the String identifying an Object that can be handled by the ResourceManager.
     *
     * @param clazz the Class Object.
     * @return the identifying String.
     * @throws RuntimeException if clazz is not a valid Object for this operation.
     */
    private static String getResourceString(Class clazz) {
        String type = treeCaches.get(clazz);
        if (type == null || type.equals("")) {
            throw new RuntimeException("Class '" + clazz + "' is not a valid request Object");
        }

        return type;
    }

    /**
     * @param type the String identifying a meta-data TreeSet in the cache.
     * @return the meta-data TreeSet (can be <b>null</b>)
     */
    @SuppressWarnings("unchecked")
    private TreeSet<AbstractMetaItem<?>> getCachedMetaDataTree(String type) {
        return metaTreeCache.get(type);
    }

    @SuppressWarnings("unchecked")
    private AbstractMetaItem<?> fetchItem(Class clazz, int id) {
        /* terminate if we do not have an internet connection */
        if (!isOnline()) {
            return null;
        }

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

    @SuppressWarnings("unchecked")
    private List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) {
        /* terminate if we do not have an internet connection */
        if (!isOnline()) {
            return null;
        }

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

            Log.d("ResourceManager", "[cleanup] '" + type + "' started");
            try {
                /* get the TreeSet from the cache */
                cachedTree = metaTreeCache.get(type);

                /* get a copy of the tree that is sorted by lastUsed */
                TreeSet<AbstractMetaItem> tree = new TreeSet<>(comparator);
                tree.addAll(cachedTree);

                /* dynamically get the dummy-constructor for the current type and create the filter */
                Constructor<?> cons = clazz.getConstructor(Date.class);
                AbstractMetaItem filterDummy = AbstractMetaItem.class.cast(cons.newInstance(cutoff));

                /* get all items last used on or before the cutoff date */
                Set<AbstractMetaItem> deleteSet = new HashSet<>(tree.headSet(tree.floor(filterDummy), true));

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
                Log.e("ResourceManager", getStackTraceAsString(e));
            }
            Log.d("ResourceManager", "[cleanup] '" + type + "' finished");
        }
    }

    @SuppressWarnings("unchecked")
    public void performCleanupTest() {
        checkInitialized();
        TreeSet<NewsMetaItem> tree;

        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Old cache content                         -");
        Log.e("Debug", "---------------------------------------------");

        tree = metaTreeCache.get(treeCaches.get(NewsMetaItem.class));
        if (tree != null) {
            for (NewsMetaItem nm : tree) {
                Log.e("Debug", nm.toString());
            }
        } else {
            Log.e("Debug", "tree == null");
        }
        tree = null;
        metaTreeCache.put(treeCaches.get(NewsMetaItem.class), null);


        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Cache content post-deletion               -");
        Log.e("Debug", "---------------------------------------------");

        tree = metaTreeCache.get(treeCaches.get(NewsMetaItem.class));
        if (tree != null) {
            for (NewsMetaItem nm : tree) {
                Log.e("Debug", nm.toString());
            }
        } else {
            Log.e("Debug", "tree == null");
        }
        tree = null;

        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Tree pre-cache                            -");
        Log.e("Debug", "---------------------------------------------");

        Calendar calendar = Calendar.getInstance();

        tree = new TreeSet<>();
        tree.add(new NewsMetaItem(1, new Date(), new Date(), "test1 - should be gone (null)", "Test", 0));

        calendar.add(Calendar.SECOND, -1);
        NewsMetaItem item = new NewsMetaItem(2, new Date(), new Date(), "test2 - should stay", "Test", 0);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MONTH, -1);
        item = new NewsMetaItem(3, new Date(), new Date(), "test3 - should be gone", "Test", 0);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MINUTE, -1);
        item = new NewsMetaItem(4, new Date(), new Date(), "test4 - should be gone", "Test", 0);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        for (NewsMetaItem nm : tree) {
            Log.e("Debug", nm.toString());
        }

        metaTreeCache.put(treeCaches.get(NewsMetaItem.class), tree);
        tree = null;

        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Tree from cache pre-cleanup               -");
        Log.e("Debug", "---------------------------------------------");

        tree = metaTreeCache.get(treeCaches.get(NewsMetaItem.class));
        for (NewsMetaItem nm : tree) {
            Log.e("Debug", nm.toString());
        }
        tree = null;

        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Tree from cache post-cleanup              -");
        Log.e("Debug", "---------------------------------------------");

        cleanup();
        tree = metaTreeCache.get(treeCaches.get(NewsMetaItem.class));
        for (NewsMetaItem nm : tree) {
            Log.e("Debug", nm.toString());
        }
    }

    @SuppressWarnings("unchecked")
    public void performTest() {
        checkInitialized();
        TreeSet<NewsMetaItem> tree = null;
        NewsItem news = null;
        String resIdent = null;
        Date d = new Date();


        metaTreeCache.put(treeCaches.get(NewsMetaItem.class), null);

        Calendar calendar = Calendar.getInstance();

        tree = new TreeSet<>();
        tree.add(new NewsMetaItem(1, new Date(), new Date(), "test1 - should be gone (null)", "Test", 0));

        calendar.add(Calendar.SECOND, -1);
        NewsMetaItem item = new NewsMetaItem(2, new Date(), new Date(), "test2 - should stay", "Test", 0);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MONTH, -1);
        item = new NewsMetaItem(3, d, d, "test3 - should be gone", "Test", 0);
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 3;
        news = new NewsItem(3, d, d, "test3 - should be gone", "Test", 0, "asdfasdfasdfadsfgkjlahsdfkljh", new byte[0]);
        itemCache.put(resIdent, news);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MINUTE, -1);
        item = new NewsMetaItem(4, d, d, "test4 - should be gone", "Test", 0);
        news = new NewsItem(4, d, d, "test4 - should be gone", "Test", 0, "asdfasdfasdf", new byte[0]);
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 4;
        itemCache.put(resIdent, news);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        metaTreeCache.put(treeCaches.get(NewsMetaItem.class), tree);
        tree = null;

        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Tree                                      -");
        Log.e("Debug", "---------------------------------------------");

        tree = metaTreeCache.get(treeCaches.get(NewsMetaItem.class));
        for (NewsMetaItem nm : tree) {
            Log.e("Debug", nm + "");
        }
        tree = null;


        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Items that should exist                    -");
        Log.e("Debug", "---------------------------------------------");
        news = null;
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 4;
        news = (NewsItem) getItem(NewsMetaItem.class, 4);
        Log.e("Debug", news + "");
        news = null;
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 3;
        news = (NewsItem) getItem(NewsMetaItem.class, 3);
        Log.e("Debug", news + "");


        Log.e("Debug", "---------------------------------------------");
        Log.e("Debug", "- Items that should not exist               -");
        Log.e("Debug", "---------------------------------------------");
        news = null;
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 5;
        news = (NewsItem) getItem(NewsMetaItem.class, 5);
        Log.e("Debug", news + "");
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + -1;
        news = (NewsItem) getItem(NewsMetaItem.class, -1);
        Log.e("Debug", news + "");

    }

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

    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz) {
        checkInitialized();

        /* get the corresponding meta-data tree */
        String type = getResourceString(clazz);
        TreeSet<AbstractMetaItem<?>> cachedTree = getCachedMetaDataTree(type);

        /* fetch meta-data from server */
        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>();
        List<AbstractMetaItem<?>> items = fetchMetaItems(clazz);

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
            Log.w("ResourceManager", "Could not fetch meta-data from server, using cached data");
            if (cachedTree == null) {
                Log.w("ResourceManager", "Cached metaTree is null");
            } else {
                Log.i("ResourceManager", "Cached metaTree size: " + cachedTree.size());
                result.addAll(cachedTree);
            }
        }

        return result;
    }

    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class clazz, AbstractMetaItem<?> first,
                                                           AbstractMetaItem<?> last,
                                                           Comparator<AbstractMetaItem<?>> comparator) {
        checkInitialized();

        TreeSet<AbstractMetaItem<?>> result = new TreeSet<>(comparator);

        // TODO: implement meta-data structure management and fetching from server

        return result;
    }
}
