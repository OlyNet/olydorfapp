package eu.olynet.olydorfapp.resource;

import android.content.Context;

import java.util.List;

import javax.ws.rs.NotFoundException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public abstract class RestManager {

    public static final int DEFAULT_RETRY_COUNT = 3;

    protected final Context context;

    /**
     * Do not use.
     */
    private RestManager() {
        this.context = null;
    }

    /**
     * Sets up the RestManager. Should be called by every subclass.
     *
     * @param context the application Context.
     */
    public RestManager(Context context) {
        this.context = context;
        this.init();
    }

    /**
     * Initializes this RestManager. Is called by the constructor and must be used to configure
     * each implementation of this abstract class.
     */
    protected abstract void init();


    /**
     * Tries to fetch the image of a specific item from the server. Defaults to 3 retries.
     *
     * @param type  the type in String form.
     * @param id    the id.
     * @param field the field name.
     * @return the fetched image or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     * @throws NotFoundException        if a HTTP 404 has been received.
     */
    public abstract byte[] fetchImage(String type, int id, String field)
            throws NoConnectionException;

    /**
     * Tries to fetch the image of a specific item from the server.
     *
     * @param type  the type in String form.
     * @param id    the id.
     * @param field the field name.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched image or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     * @throws NotFoundException        if a HTTP 404 has been received.
     */
    public abstract byte[] fetchImage(String type, int id, String field, int retryCount)
            throws NoConnectionException;

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
    public abstract AbstractMetaItem<?> fetchItem(Class clazz, int id) throws NoConnectionException;

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
    public abstract AbstractMetaItem<?> fetchItem(Class clazz, int id, int retryCount) throws
                                                                                       NoConnectionException;

    /**
     * Tries to fetch all items of a specific type from the server. Defaults to 3 retries.
     *
     * @param clazz the Class of the items to be fetched. Must be specified within the treeCaches
     *              Map.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    public abstract List<AbstractMetaItem<?>> fetchItems(Class clazz) throws NoConnectionException;

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
    public abstract List<AbstractMetaItem<?>> fetchItems(Class clazz, int retryCount) throws
                                                                                      NoConnectionException;

    /**
     * Tries to fetch all items of a specific type from the server. Defaults to 3 retries.
     *
     * @param clazz the Class of the items to be fetched. Must be specified within the treeCaches
     *              Map.
     * @param ids   the List containing the ids to fetch from the server. Must not contain any
     *              <b>null</b> elements.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    public abstract List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids)
            throws NoConnectionException;

    /**
     * Tries to fetch all items of a specific type from the server.
     *
     * @param clazz      the Class of the items to be fetched. Must be specified within the
     *                   treeCaches Map.
     * @param ids        the List containing the ids to fetch from the server. Must not contain any
     *                   <b>null</b> elements.
     * @param retryCount how many times a fetch should be retried if it failed.
     * @return the fetched items or <b>null</b> if this operation was not successful.
     * @throws IllegalArgumentException if clazz is not a valid Class for this operation.
     * @throws NoConnectionException    if no internet connection is available.
     */
    public abstract List<AbstractMetaItem<?>> fetchItems(Class clazz, List<Integer> ids,
                                                         int retryCount) throws
                                                                         NoConnectionException;

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
    public abstract AbstractMetaItem<?> fetchMetaItem(Class clazz, int id) throws
                                                                           NoConnectionException;

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
    public abstract AbstractMetaItem<?> fetchMetaItem(Class clazz, int id, int retryCount) throws
                                                                                           NoConnectionException;

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
    public abstract List<AbstractMetaItem<?>> fetchMetaItems(Class clazz) throws
                                                                          NoConnectionException;

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
    public abstract List<AbstractMetaItem<?>> fetchMetaItems(Class clazz, int retryCount) throws
                                                                                          NoConnectionException;


}
