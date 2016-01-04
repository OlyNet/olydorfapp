package eu.olynet.olydorfapp.resources;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.KeyStore;
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
    private ResteasyClient client;

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
            DualCacheLogUtils.enableLog();
            DualCacheContextUtils.setContext(this.context);
            metaTreeCache = new DualCacheBuilder<TreeSet>("MetaTrees", pInfo.versionCode, TreeSet.class)
                    .useDefaultSerializerInRam(5 * 1024 * 1024)
                    .useDefaultSerializerInDisk(10 * 1024 * 1024, true);
            itemCache = new DualCacheBuilder<AbstractMetaItem>("Items", pInfo.versionCode, AbstractMetaItem.class)
                    .useDefaultSerializerInRam(5 * 1024 * 1024)
                    .useDefaultSerializerInDisk(50 * 1024 * 1024, true);
            Log.i("ResourceManager.init", "DualCache setup complete.");

            /* setup ResteasyClient */
            try {
                /* create a KeyStore that contains our client certificate */
                //InputStream certificate = this.context.getAssets().open(CERTIFICATE_FILE);
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(null);
                //keyStore.load(certificate, CERTIFICATE_KEY);

                ClientHttpEngine engine = new URLConnectionEngine();
                client = new ResteasyClientBuilder().httpEngine(engine)
                        .keyStore(keyStore, new char[0])
                        .build();
                Log.i("ResourceManager.init", "ResteasyClient setup complete.");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }


            this.initialized = true;
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
    }

    private AbstractMetaItem<?> fetchItem(Class clazz, long id) {
        // TODO: implement fetching items from the server
        Log.i("ResourceManager", "fetchItem(" + clazz + ", " + id + ");");

        return null;
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

            Log.i("ResourceManager", "[cleanup] " + type + " started");
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
                e.printStackTrace();
            }
            Log.i("ResourceManager", "[cleanup] " + type + " finished");
        }
    }

    @SuppressWarnings("unchecked")
    public void performCleanupTest() {
        checkInitialized();
        TreeSet<NewsMetaItem> tree = null;

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
        tree.add(new NewsMetaItem(1, new Date(), new Date(), "test1 - should be gone (null)", "Test", null));

        calendar.add(Calendar.SECOND, -1);
        NewsMetaItem item = new NewsMetaItem(2, new Date(), new Date(), "test2 - should stay", "Test", null);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MONTH, -1);
        item = new NewsMetaItem(3, new Date(), new Date(), "test3 - should be gone", "Test", null);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MINUTE, -1);
        item = new NewsMetaItem(4, new Date(), new Date(), "test4 - should be gone", "Test", null);
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
        tree.add(new NewsMetaItem(1, new Date(), new Date(), "test1 - should be gone (null)", "Test", null));

        calendar.add(Calendar.SECOND, -1);
        NewsMetaItem item = new NewsMetaItem(2, new Date(), new Date(), "test2 - should stay", "Test", null);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MONTH, -1);
        item = new NewsMetaItem(3, d, d, "test3 - should be gone", "Test", null);
        resIdent = treeCaches.get(NewsMetaItem.class) + "_" + 3;
        news = new NewsItem(3, d, d, "test3 - should be gone", "Test", null, "asdfasdfasdfadsfgkjlahsdfkljh");
        itemCache.put(resIdent, news);
        item.setLastUsed(calendar.getTime());
        tree.add(item);

        calendar.add(Calendar.MINUTE, -1);
        item = new NewsMetaItem(4, d, d, "test4 - should be gone", "Test", null);
        news = new NewsItem(4, d, d, "test4 - should be gone", "Test", null, "asdfasdfasdf");
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
    public AbstractMetaItem<?> getItem(Class<?> clazz, long id) {
        checkInitialized();

        /* get the corresponding meta-data tree */
        String type = treeCaches.get(clazz);
        if (type == null) {
            Log.e("ResourceManager", "meta-data tree for " + clazz + " not found");
            return null;
        }
        TreeSet<AbstractMetaItem> tree = metaTreeCache.get(type);
        if (tree == null) {
            Log.e("ResourceManager", "meta-data tree for " + clazz + " not found");
            return null;
        }


        try {
            /* create a dummy item with the same id */
            Constructor<?> cons = clazz.getConstructor(long.class);
            AbstractMetaItem<?> dummyItem = (AbstractMetaItem<?>) cons.newInstance(id);

            /* use the dummy item to search for the real item within the tree */
            AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
            if (metaItem == null || metaItem.getId() != id || metaItem.getLastUpdated() == null) {
                Log.e("ResourceManager", "meta-data tree " + clazz
                        + " does not contain the requested element " + id);
                return null;
            }

            /* check cache and query server on miss or date mismatch */
            String resIdent = treeCaches.get(clazz) + "_" + id;
            AbstractMetaItem<?> item = (AbstractMetaItem<?>) itemCache.get(resIdent);
            if (item == null || !item.getLastUpdated().equals(metaItem.getLastUpdated())) {
                AbstractMetaItem<?> webItem = fetchItem(clazz, id);

                /* update local cache if fetch was successful */
                if (webItem != null) {
                    Method updateItem = clazz.getMethod("updateItem", clazz);
                    updateItem.invoke(clazz.cast(metaItem), clazz.cast(webItem));

                    itemCache.put(resIdent, webItem);
                    metaTreeCache.put(type, tree);

                    return webItem;
                }
            }

            return item;
        } catch (Exception e) {
            /* lord have mercy */
            e.printStackTrace();
            return null;
        }
    }

    public List<AbstractMetaItem<?>> getListOfMetaItems(Class clazz, AbstractMetaItem<?> first,
                                                        AbstractMetaItem<?> last,
                                                        Comparator<? extends AbstractMetaItem<?>> comparator) {
        checkInitialized();

        // TODO: implement meta-data structure management and fetching from server

        return null;
    }
}


interface OlyNetClient {

    // TODO: implement ResteasyClient
}
