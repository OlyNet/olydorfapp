package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vincentbrison.openlibraries.android.dualcache.lib.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheBuilder;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheContextUtils;
import com.vincentbrison.openlibraries.android.dualcache.lib.DualCacheLogUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class DualCacheManager extends AbstractCacheManager {

    /**
     * The time in minutes after which a cache entry is considered stale.
     */
    private static final int MINUTES_UNTIL_CACHE_STALE = 60;
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
     * Sets up a DualCacheManager
     *
     * @param context the application Context.
     */
    public DualCacheManager(Context context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    protected void init() {
        /* dynamically get the PackageInformation */
        PackageInfo pInfo;
        try {
            pInfo = this.context.getPackageManager()
                                .getPackageInfo(this.context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        /* setup DualCache */
        DualCacheLogUtils.enableLog();
        DualCacheContextUtils.setContext(this.context);
        CacheSerializer<TreeSet> treeSerializer = new CacheSerializer<>(TreeSet.class);
        metaTreeCache = new DualCacheBuilder<>("MetaTrees", pInfo.versionCode,
                                               TreeSet.class).useCustomSerializerInRam(
                1024 * 1024, treeSerializer)
                                                             .useCustomSerializerInDisk(
                                                                     10 * 1024 * 1024, true,
                                                                     treeSerializer);
        CacheSerializer<AbstractMetaItem> itemSerializer = new CacheSerializer<>(
                AbstractMetaItem.class);
        itemCache = new DualCacheBuilder<>("Items", pInfo.versionCode,
                                           AbstractMetaItem.class).useCustomSerializerInRam(
                500 * 1024, itemSerializer)
                                                                  .useCustomSerializerInDisk(
                                                                          200 * 1024 * 1024, true,
                                                                          itemSerializer);
        cacheStaleCache = new DualCacheBuilder<>("Stale", pInfo.versionCode,
                                                 Map.class).useDefaultSerializerInRam(
                50 * 1024).useDefaultSerializerInDisk(5 * 1024 * 1024, true);
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
    }

    @Override
    public void invalidate() {
        metaTreeCache.invalidate();
        itemCache.invalidate();
        cacheStaleCache.invalidate();
        cacheLastUpdated = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getCachedMetaDataTree(Class<?> clazz) {
        return metaTreeCache.get(ResourceManager.getResourceString(clazz));
    }

    @Override
    public void putCachedMetaDataTree(Class<?> clazz, @Nullable TreeSet<AbstractMetaItem<?>> tree) {
        metaTreeCache.put(ResourceManager.getResourceString(clazz), tree);
    }

    @Override
    public void setCacheLastUpdated(Class<?> clazz) {
        cacheLastUpdated.put(ResourceManager.getResourceString(clazz), new Date());
        cacheStaleCache.put("stale", cacheLastUpdated);
    }

    @Override
    public boolean isCacheStale(Class<?> clazz) {
        Date cacheUpdateDate = cacheLastUpdated.get(ResourceManager.getResourceString(clazz));
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

    @Override
    public AbstractMetaItem<?> getCachedItem(Class<?> clazz, int id) {
        return this.itemCache.get(ResourceManager.getResourceString(clazz) + "_" + id);
    }

    @Override
    public void putCachedItem(Class<?> clazz, @NonNull AbstractMetaItem<?> item) {
        this.itemCache.put(ResourceManager.getResourceString(clazz) + "_" + item.getId(), item);
    }

    @Override
    public void deleteCachedItem(Class<?> clazz, int id) {
        this.itemCache.put(ResourceManager.getResourceString(clazz) + "_" + id, null);
    }

}
