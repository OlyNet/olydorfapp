/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.NotFoundException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * The ResourceManager is the Singleton interface for accessing data on the OlyNet servers.
 * Requested data is cached using the DualCache library and provided as needed. The ResourceManager
 * has to be initialized with the application Context by calling its <i>init(Context)</i> method
 * prior to use. If this is neglected, a RuntimeException will be thrown.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class ProductionResourceManager extends ResourceManager {

    /**
     * The timeout in seconds after each message to the user.
     */
    private static final int TIMEOUT_AFTER_MESSAGE = 15;
    /**
     * Singleton instance
     */
    private static ProductionResourceManager ourInstance = new ProductionResourceManager();
    /**
     * The RestManager.
     */
    private RestManager rest = null;
    /**
     * The CacheManager.
     */
    private AbstractCacheManager cache = null;
    /**
     * The application Context.
     */
    private Context context;
    /**
     * The last time the user has been notified by calling informUser().
     */
    private Date lastNotifiedDate = null;

    /**
     * Empty constructor. The real work is done by the init() method.
     */
    private ProductionResourceManager() {
        super();
    }

    /**
     * @return the instance of the ResourceManager Singleton.
     */
    public static ProductionResourceManager getInstance() {
        return ourInstance;
    }

    @Override
    public void init(Context context) {
        if (!isInitialized()) {
            this.context = context.getApplicationContext();

            /* setup cache */
            this.cache = new DualCacheManager(context);

            /* setup rest */
            this.rest = new ProductionRestManager(context);
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
    }

    /**
     * Has the ResourceManager been properly initialized?
     *
     * @return <b>true</b> if and only if the ResourceManager has been properly initialized.
     */
    public boolean isInitialized() {
        return this.cache != null && this.rest != null;
    }

    /**
     * Checks whether the ResourceManager has been correctly initialized. Must be called at the
     * beginning of every public non-static function of this class.
     *
     * @throws IllegalStateException if this is not the case.
     */
    private void abortIfNotInitialized() {
        if (!isInitialized()) {
            throw new IllegalStateException("The ResourceManager has not been initialized." +
                                            "Initialize it by calling 'ResourceManager" +
                                            ".getInstance().init(this); '" +
                                            "in the MainActivity's onCreate()!");
        }
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
     * Invalidates the cache.
     */
    public void invalidateCache() {
        cache.invalidate();
    }

    @Override
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
                    cachedTree = this.cache.getCachedMetaDataTree(clazz);
                    if (cachedTree == null) {
                        Log.w("ResourceManager", "cleanup of '" + clazz + "' failed, tree is null");
                        continue;
                    }

                    /* get a copy of the tree that is sorted by lastUsedDate */
                    TreeSet<AbstractMetaItem<?>> tree = new TreeSet<>(comparator);
                    tree.addAll(cachedTree);

                    /* create a dummy element with the specific lastUsedDate */
                    AbstractMetaItem<?> filterDummy = new AbstractMetaItem.DummyFactory(
                            clazz).setLastUsedDate(cutoff).build();

                    /* get all items last used on or before the cutoff createDate */
                    Set<AbstractMetaItem<?>> deleteSet = new HashSet<>(
                            tree.headSet(tree.floor(filterDummy), true));

                    /* delete all cached entries of the full objects */
                    for (AbstractMetaItem metaItem : deleteSet) {
                        this.cache.deleteCachedItem(clazz, metaItem.getId());
                    }

                    /* remove all references to deleted items from the meta-tree */
                    tree.removeAll(deleteSet);

                    /* write the pruned tree back to cache */
                    this.cache.putCachedMetaDataTree(clazz, tree);
                } catch (Exception e) {
                    throw new RuntimeException("cleanup of '" + clazz + "' failed", e);
                }
                Log.d("ResourceManager", "cleanup of '" + clazz + "' finished");
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractMetaItem<?> getItem(Class<?> clazz, int id) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> tree = this.cache.getCachedMetaDataTree(clazz);
            if (tree == null) {
                throw new RuntimeException("meta-data tree for '" + clazz + "' not found");
            }

            try {
                /* create a dummy item with the same id */
                AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz).setId(id)
                                                                                        .build();

                /* use the dummy item to search for the real item within the tree */
                AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
                if (metaItem == null || metaItem.getId() != id || metaItem.getEditDate() == null) {
                    throw new RuntimeException("meta-data tree of type '" + clazz +
                                               "' does not contain the requested element " + id);
                }

                /* check cache and query server on miss or createDate mismatch */
                AbstractMetaItem<?> item = this.cache.getCachedItem(clazz, id);
                if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                    Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                    AbstractMetaItem<?> webItem;
                    try {
                        webItem = rest.fetchItem(clazz, id);
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
                        Log.w("ResourceManager",
                              "Fetch failed for some reason (getItem) " + clazz + id);
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
                    Log.d("ResourceManager",
                          "Updating cache for item with id " + id + " and meta-data tree '" +
                          clazz + "'");
                    this.cache.putCachedItem(clazz, item);
                }

                /* write meta-data tree to cache */
                Log.d("ResourceManager", "Updating meta-data tree cache for '" + clazz + "'");
                this.cache.putCachedMetaDataTree(clazz, tree);

                return item;
            } catch (Exception e) {
                Log.e("ResourceManager", "exception information if it gets wrapped to often", e);
                throw new RuntimeException("during the getItem for '" + clazz + "' id " + id, e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz, List<Integer> ids,
                                              Comparator<AbstractMetaItem<?>> comparator) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> tree = this.cache.getCachedMetaDataTree(clazz);
            if (tree == null) {
                throw new RuntimeException(
                        "meta-data tree for '" + clazz + "' not found, creating it");
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
                    AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz).setId(
                            id).build();

                    /* use the dummy item to search for the real item within the tree */
                    AbstractMetaItem<?> metaItem = tree.floor(dummyItem);
                    if (metaItem == null || metaItem.getId() != id ||
                        metaItem.getEditDate() == null) {
                        throw new RuntimeException("meta-data tree of type '" + clazz +
                                                   "' does not contain the requested element " +
                                                   id);
                    }

                    /* check cache and query server on miss or editDate mismatch */
                    AbstractMetaItem<?> item = this.cache.getCachedItem(clazz, id);
                    if (item == null || !item.getEditDate().equals(metaItem.getEditDate())) {
                        Log.i("ResourceManager", "Cached item is outdated, fetch necessary");
                        AbstractMetaItem<?> webItem;
                        try {
                            webItem = rest.fetchItem(clazz, id);
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
                            Log.w("ResourceManager", "Fetch failed for some reason (getItems)");
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
                        Log.d("ResourceManager",
                              "Updating cache for item " + id + " of type " + clazz);
                        this.cache.putCachedItem(clazz, item);

                        /* add item to the Collection of items to be returned */
                        itemTree.add(item);
                    }
                }
            } catch (Exception e) {
                Log.e("ResourceManager", "exception information in case it gets wrapped to often",
                      e);
                throw new RuntimeException("during the getItems() for '" + clazz + "' with ids: " +
                                           Arrays.toString(ids.toArray()), e);
            }

            Log.d("ResourceManager", "Updating meta-data tree cache of type '" + clazz + "'");
            this.cache.putCachedMetaDataTree(clazz, tree);


            /* convert and return result */
            return new ArrayList<>(itemTree);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, boolean forceUpdate) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            /* get the corresponding meta-data tree */
            TreeSet<AbstractMetaItem<?>> cachedTree = this.cache.getCachedMetaDataTree(clazz);

            /* fetch meta-data from server */
            List<AbstractMetaItem<?>> items = null;
            boolean noConnection = false;
            boolean generalError = false;
            if (this.cache.isCacheStale(clazz) || forceUpdate || cachedTree == null) {
                try {
                    items = rest.fetchMetaItems(clazz);
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
                Log.d("ResourceManager",
                      "Received " + items.size() + " meta-data items from server");

                /* add all items to the result to be returned */
                result.addAll(items);

                /* delete all no longer existing items */
                if (cachedTree != null) {
                    cachedTree.removeAll(result);
                    for (AbstractMetaItem<?> metaItem : cachedTree) {
                        Log.d("ResourceManager",
                              "Deleting cached item " + metaItem.getId() + " of type '" + clazz +
                              "'");
                        this.cache.deleteCachedItem(clazz, metaItem.getId());
                    }
                }

                /* write cache */
                this.cache.putCachedMetaDataTree(clazz, result);
                this.cache.setCacheLastUpdated(clazz);
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

    @Override
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, int limit,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable
                                                           Comparator<AbstractMetaItem<?>>
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
            preResult.removeAll(sortedMetaTree.headSet(sortedMetaTree.floor(after), true));
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

    @Override
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable AbstractMetaItem<?> before,
                                                           @Nullable
                                                           Comparator<AbstractMetaItem<?>>
                                                                   comparator,
                                                           boolean forceUpdate) {
        abortIfNotInitialized();

        /* lock for thread-safety */
        synchronized (lockMap.get(clazz)) {
            throw new UnsupportedOperationException("not implemented yet");
            // TODO: implement meta-data structure management and fetching from server
        }
    }

    @Override
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, int limit,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable
                                                           Comparator<AbstractMetaItem<?>>
                                                                   comparator,
                                                           @Nullable
                                                           OrganizationMetaItem filterOrganization,
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
            preResult.removeAll(sortedMetaTree.headSet(sortedMetaTree.floor(after), true));
        }

        /* only take a specified number of items from the results */
        if (limit > 0) {
            int n = 0;
            for (AbstractMetaItem<?> item : preResult) {
                if (n++ >= limit) {
                    break;
                }

                /* compare organizations */
                if (item.getOrganization() == null) {
                    if (filterOrganization == null) {
                        result.add(item); /* both null */
                    }
                } else if (item.getOrganization().equals(filterOrganization)) {
                    result.add(item); /* equal */
                } else {
                    Log.d("ResourceManager", "Dropping due to organization mismatch: " + item);
                }
            }
        } else {
            result.addAll(preResult);
        }

        return result;
    }
}
