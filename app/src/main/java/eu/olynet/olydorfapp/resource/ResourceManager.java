package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public abstract class ResourceManager {

    /**
     * The static Map mapping the valid Classes to their corresponding identifier Strings.
     * <p/>
     * All items that need to be available via this Class have to be added in the static{...}
     * section below.
     *
     * @see eu.olynet.olydorfapp.resource.OlyNetClient
     * @see eu.olynet.olydorfapp.model.AbstractMetaItemMixIn
     */
    public static final Map<Class, String> treeCaches;

    /**
     * A static Map mapping the valid Classes to their corresponding locking Objects. Locking on the
     * Classes directly would also be possible, but could lead to problems if they were also used in
     * some other place.
     */
    public static final Map<Class, Object> lockMap;

    /**
     * All items contained in this Set will be skipped during cleanup operations.
     */
    public static final Set<Class> skipDuringCleanup;

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
     * Returns the String identifying an Object that can be handled by the ResourceManager.
     *
     * @param clazz the Class Object.
     * @return the identifying String.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public static String getResourceString(Class clazz) {
        String type = treeCaches.get(clazz);
        if (type == null || type.equals("")) {
            throw new IllegalArgumentException(
                    "Class '" + clazz + "' is not a valid request Object");
        }

        return type;
    }

    /**
     * Initializes the ResourceManager with the Context. This has to be called before any other
     * operation on the ResourceManager. Failure to do so will cause a RuntimeException. Duplicate
     * calls to this function are not recommended and will result in a warning.
     *
     * @param context the application Context.
     */
    public abstract void init(Context context);

    /**
     * Performs a cleanup of all caches. Everything that has not been used in the last month will be
     * purged.
     *
     * @throws IllegalStateException if the ResourceManager has not been initialized correctly.
     */
    public abstract void cleanup();

    /**
     * Get the image of a specific item from the server.
     *
     * @param type  the type in String form.
     * @param id    the id.
     * @param field the field name.
     * @return the image requested or <b>null</b>.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     * @throws RuntimeException         if some weird Reflection error occurs.
     */
    public abstract byte[] getImage(String type, int id, String field) throws NoConnectionException;

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
     * @throws RuntimeException         if the ID requested is not present in the meta-data tree or
     *                                  the whole tree itself is missing.
     * @throws RuntimeException         if some weird Reflection error occurs.
     */
    public abstract AbstractMetaItem<?> getItem(Class<?> clazz, int id);

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
    public abstract List<AbstractMetaItem<?>> getItems(Class<?> clazz, List<Integer> ids,
                                                       Comparator<AbstractMetaItem<?>> comparator);

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
    public abstract TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz,
                                                                    boolean forceUpdate);

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
    public abstract TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, long limit,
                                                                    @Nullable
                                                                    AbstractMetaItem<?> after,
                                                                    @Nullable
                                                                    Comparator<AbstractMetaItem<?>> comparator,
                                                                    boolean forceUpdate);

    /**
     * Returns a certain number of items of a specific type that are all greater (according to the
     * ordering) then a specified element. Also filters out anything whose Organization does not
     * match the provided OrganizationMetaItem.
     *
     * @param clazz              the Class of the item to be fetched. Must be specified within the
     *                           treeCaches Map.
     * @param after              exclusive lower bound. Can be null or a dummy.
     * @param limit              Number of elements that are returned. If it is below or equal to
     *                           zero, no
     *                           limit is imposed.
     * @param comparator         the Comparator used for ordering the meta-data tree. Can be null
     *                           for
     *                           default ordering.
     * @param filterOrganization the OrganizationMetaItem the requested MetaItems must match.
     * @param forceUpdate        whether the meta-data should always fetched from the server.
     * @return the requested meta-data. This does not necessarily have to be up-to-createDate. If
     * the server cannot be reached in time, a cached version will be returned instead.
     * @throws IllegalStateException    if the ResourceManager has not been initialized correctly.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     */
    public abstract TreeSet<AbstractMetaItem<?>> getTreeOfMetaItems(Class<?> clazz, long limit,
                                                                    @Nullable
                                                                    AbstractMetaItem<?> after,
                                                                    @Nullable
                                                                    Comparator<AbstractMetaItem<?>> comparator,
                                                                    ItemFilter filter,
                                                                    boolean forceUpdate);
}
