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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.TreeSet;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
abstract class AbstractCacheManager {

    final Context context;

    /**
     * Do not use.
     */
    private AbstractCacheManager() {
        this.context = null;
    }

    /**
     * Sets up the CacheManager. Should be called by every subclass.
     *
     * @param context the application Context.
     */
    AbstractCacheManager(Context context) {
        this.context = context;
        this.init();
    }

    /**
     * Initializes this CacheManager. Is called by the constructor and must be used to configure
     * each implementation of this abstract class.
     */
    protected abstract void init();

    /**
     * Invalidates the cache.
     */
    public abstract void invalidate();

    /**
     * @param clazz the Class of the MetaItem.
     * @return the meta-data TreeSet (can be <b>null</b>)
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract TreeSet<AbstractMetaItem<?>> getCachedMetaDataTree(Class<?> clazz);

    /**
     * Sets the cache entry for a specific MetaItem TreeSet.
     *
     * @param clazz the Class of the MetaItem.
     * @param tree  the TreeSet to write to cache. Setting this to <b>null</b> deletes the entry.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract void putCachedMetaDataTree(Class<?> clazz,
                                               @Nullable TreeSet<AbstractMetaItem<?>> tree);

    /**
     * Sets the corresponding cacheLastUpdated Date to now.
     *
     * @param clazz the Class of the MetaItem.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract void setCacheLastUpdated(Class<?> clazz);

    /**
     * Check whether a meta-data TreeSet's cache entry is stale.
     *
     * @param clazz the Class of the MetaItem.
     * @return <b>true</b> if it is stale, <b>false</b> otherwise.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract boolean isCacheStale(Class<?> clazz);

    /**
     * Returns a specific item from the cache.
     *
     * @param clazz the Class of the associated MetaItem
     * @param id    the ID identifying the item.
     * @return the requested item. Can be <b>null</b> if it is not present in the cache.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract AbstractMetaItem<?> getCachedItem(Class<?> clazz, int id);

    /**
     * Updates the cache entry for a specific item.
     *
     * @param clazz the Class of the associated MetaItem
     * @param item  the item to put in the cache. Must not be <b>null</b>.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract void putCachedItem(Class<?> clazz, @NonNull AbstractMetaItem<?> item);

    /**
     * Deletes a cache entry for a specific item.
     *
     * @param clazz the Class of the associated MetaItem
     * @param id    the unique ID identifying the item.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract void deleteCachedItem(Class<?> clazz, int id);

}
