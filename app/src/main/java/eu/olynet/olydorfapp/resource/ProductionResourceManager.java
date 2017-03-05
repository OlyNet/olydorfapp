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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.ImageDeserializer;

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
    private static final ProductionResourceManager ourInstance = new ProductionResourceManager();

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
     *
     */
    private final Map<AbstractMetaItem<?>, List<ImageListener>> listenerMap = new LinkedHashMap<>();

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
            this.rest = new RetrofitRestManager(context);
        } else {
            Log.w("ResourceManager", "Duplicate init");
        }
    }

    @Override
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
            handler.post(() -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show());

            /* update the date */
            lastNotifiedDate = new Date();
        }
    }

    /**
     * Handles the event that a client certificate is not accepted by the server.
     *
     * @param e the Exception that caused this method call. May be null.
     */
    private void handleClientCertError(@Nullable Throwable e) {
        /* detailed log */
        if (e == null) {
            Log.e("ResourceManager", "Client certificate not accepted!");
        } else {
            Log.e("ResourceManager", "Client certificate not accepted!", e);
        }

        /* inform the user */
        informUser(this.context.getString(R.string.resourcemanager_client_certificate));
    }

    /**
     * Invalidates the cache.
     */
    @SuppressWarnings("unused")
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
    public byte[] getImage(String type, int id, String field) throws NoConnectionException {
        abortIfNotInitialized();

        byte[] image = null;
        try {
            image = this.rest.fetchImage(type, id, field);
        } catch (ClientCertificateInvalidException e) {
            e.printStackTrace();
            handleClientCertError(e);
        }
        return image;
    }

    @Override
    public void getImageAsync(String type, int id, String field) {
        abortIfNotInitialized();

        this.rest.fetchImageAsync(type, id, field);
    }

    @Override
    void asyncReceptionHook(String type, int id, byte[] image) {
        /* get the necessary classes */
        Class<?> clazz = treeCaches.getKey(type);
        Class<?> fullClazz = metaToFullClassMap.get(clazz);

        /* get the correct item form the cache */
        AbstractMetaItem<?> item = this.cache.getCachedItem(clazz, id);

        /* update the image of the item via Reflection magic */
        try {
            Method method = fullClazz.getMethod("setImage", byte[].class);
            method.invoke(item, image);
        } catch (Exception e) { /* pray that nothing goes wrong */
            throw new RuntimeException(e);
        }

        /* update lastUseDate */
        item.setLastUsedDate();

        /* write updated item back to cache */
        this.cache.putCachedItem(clazz, item);

        /* notify */
        notifyListeners(item);
    }

    /**
     * Notifies all registered ImageListeners for a specified AbstractMetaItem.
     *
     * @param item the AbstractMetaItem for which the ImageListeners are to be notified.
     */
    private void notifyListeners(AbstractMetaItem<?> item) {
        synchronized (this.listenerMap) {
            /* get all ImageListeners for this item and notify them */
            List<ImageListener> listeners = this.listenerMap.get(item);
            if (listeners != null) {
                for (ImageListener listener : listeners) {
                    listener.onImageLoad(item);
                }

                /* remove all listeners */
                this.listenerMap.remove(item);
            } else {
                Log.w("ResourceManager", "listeners is null");
            }
        }
    }

    @Override
    public void registerImageListener(ImageListener listener, AbstractMetaItem<?> item) {
        synchronized (this.listenerMap) {
            List<ImageListener> listeners = this.listenerMap.get(item);

            if (listeners == null) {
                listeners = new ArrayList<>();
            }
            listeners.add(listener);

            this.listenerMap.put(item, listeners);
        }

        /* asynchronously check if the fetch already went through */
        new AsyncTask<Void, Void, AbstractMetaItem<?>>() {
            protected AbstractMetaItem<?> doInBackground(Void... params) {
                AbstractMetaItem<?> cacheItem = cache.getCachedItem(
                        metaToFullClassMap.getKey(item.getClass()), item.getId());
                byte[] image;
                try {
                    Method method = item.getClass().getMethod("getImage");
                    image = (byte[]) method.invoke(cacheItem);
                } catch (Exception e) { /* pray that nothing goes wrong */
                    throw new RuntimeException(e);
                }

                return !Arrays.equals(image, ImageDeserializer.MAGIC_VALUE) ? cacheItem : null;
            }

            protected void onPostExecute(AbstractMetaItem<?> item) {
                if (item != null) {
                    notifyListeners(item);
                }
            }
        }.execute();
    }

    @Override
    public void unregisterImageListener(ImageListener listener, AbstractMetaItem<?> item) {
        synchronized (this.listenerMap) {
            List<ImageListener> listeners = this.listenerMap.get(item);
            if (listeners != null) {
                listeners.remove(listener);

                if (listeners.isEmpty()) {
                    this.listenerMap.remove(item);
                } else {
                    this.listenerMap.put(item, listeners);
                }
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
                    Log.i("ResourceManager", "Cached item is outdated or missing, fetch necessary");
                    AbstractMetaItem<?> webItem;
                    try {
                        webItem = rest.fetchItem(clazz, id);
                    } catch (Http404Exception e) {
                        webItem = null;
                        Log.i("ResourceManager", "Received 404 for item " + id + " of '"
                                                 + clazz + "'");

                        /* remove the item from the meta-data tree */
                        tree.remove(dummyItem);
                    } catch (NoConnectionException e) {
                        webItem = null;
                        Log.w("ResourceManager", "NoConnectionException");
                    } catch (ClientCertificateInvalidException e) {
                        e.printStackTrace();
                        handleClientCertError(e);
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
    public List<AbstractMetaItem<?>> getItems(Class<?> clazz, Collection<Integer> ids,
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
                List<Integer> idsToFetch = new ArrayList<>();
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
                        idsToFetch.add(id);
                    } else {
                        /* update last used */
                        item.setLastUsedDate();

                        /* update meta-data tree */
                        Method updateItem = clazz.getMethod("updateItem", clazz);
                        updateItem.invoke(clazz.cast(metaItem), clazz.cast(item));

                        /* write updated item to cache */
                        Log.d("ResourceManager", "Updating cache for item " + id + " of type "
                                                 + clazz);
                        this.cache.putCachedItem(clazz, item);

                        /* add item to the Collection of items to be returned */
                        itemTree.add(item);
                        Log.d("ResourceManager", "Cached item " + id + " of type '" + clazz +
                                                 "' was up-to-createDate, no fetch necessary");
                    }
                }

                /* check if we need to fetch additional items from the server */
                if (!idsToFetch.isEmpty()) {
                    List<AbstractMetaItem<?>> fetchedItems;
                    try {
                        if (idsToFetch.size() == 1) {
                            /* use the single-item endpoint if we only want one */
                            fetchedItems = new ArrayList<>();
                            AbstractMetaItem<?> item = rest.fetchItem(clazz, idsToFetch.get(0));
                            if (item != null) {
                                fetchedItems.add(item);
                            }
                        } else {
                            /* use any otherwise */
                            fetchedItems = rest.fetchItems(clazz, idsToFetch);
                        }
                    } catch (NoConnectionException e) {
                        fetchedItems = null;
                        Log.w("ResourceManager", "NoConnectionException");
                    } catch (ClientCertificateInvalidException e) {
                        e.printStackTrace();
                        handleClientCertError(e);
                        fetchedItems = null;
                    }

                    if (fetchedItems != null) {
                        Map<Integer, AbstractMetaItem<?>> fetchedItemsMap = new TreeMap<>();
                        for (AbstractMetaItem<?> item : fetchedItems) {
                            fetchedItemsMap.put(item.getId(), item);
                        }

                        for (int id : idsToFetch) {
                            /* create a dummy item */
                            AbstractMetaItem<?> dummyItem = new AbstractMetaItem.DummyFactory(clazz)
                                    .setId(id).build();

                            /* get the matching meta item from the meta-data tree */
                            AbstractMetaItem<?> metaItem = tree.floor(dummyItem);

                            /* see if the requested item is present in the fetchedItems */
                            AbstractMetaItem<?> fetchedItem = fetchedItemsMap.get(id);
                            if (fetchedItem != null) {
                                /* update last used */
                                fetchedItem.setLastUsedDate();

                                /* update meta-data tree */
                                Method updateItem = clazz.getMethod("updateItem", clazz);
                                updateItem.invoke(clazz.cast(metaItem), clazz.cast(fetchedItem));

                                /* write updated item to cache */
                                Log.d("ResourceManager",
                                      "Updating cache for item " + id + " of type " + clazz);
                                this.cache.putCachedItem(clazz, fetchedItem);

                                /* add item to the Collection of items to be returned */
                                itemTree.add(fetchedItem);
                            } else {
                                /* absence implies that the item has been deleted from the server */
                                tree.remove(dummyItem);
                            }
                        }
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
            boolean certificateError = false;
            if (this.cache.isCacheStale(clazz) || forceUpdate || cachedTree == null) {
                try {
                    items = rest.fetchMetaItems(clazz);
                } catch (NoConnectionException nce) {
                    noConnection = true;
                } catch (ClientCertificateInvalidException e) {
                    e.printStackTrace();
                    certificateError = true;
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
                } else if (certificateError) {
                    handleClientCertError(null);
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
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, long limit,
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
            long n = 0;
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
    public TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, long limit,
                                                           @Nullable AbstractMetaItem<?> after,
                                                           @Nullable
                                                                   Comparator<AbstractMetaItem<?>>
                                                                   comparator,
                                                           ItemFilter filter,
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
        long n = 0;
        for (AbstractMetaItem<?> item : preResult) {
            if (limit > 0 && n >= limit) {
                break;
            }

            /* apply filter and add only if it matches */
            if (filter.test(item)) {
                result.add(item);
                n++;
            } else {
                Log.d("ResourceManager", "Dropped due to filter mismatch:" + item);
            }
        }

        return result;
    }
}
