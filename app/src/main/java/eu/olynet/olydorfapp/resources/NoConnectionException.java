package eu.olynet.olydorfapp.resources;

import java.util.TreeSet;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NoConnectionException extends Exception {

    private TreeSet<AbstractMetaItem<?>> cachedResult;

    public NoConnectionException() {
        super();
    }

    public NoConnectionException(String message) {
        super(message);
    }

    public TreeSet<AbstractMetaItem<?>> getCachedResult() {
        return cachedResult;
    }

    public void setCachedResult(TreeSet<AbstractMetaItem<?>> cachedResult) {
        this.cachedResult = cachedResult;
    }
}
