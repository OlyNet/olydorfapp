package eu.olynet.olydorfapp.resource;

import java.util.EventObject;

/**
 * Created by Martin on 25.03.2015.
 */
public class ResourceEvent extends EventObject {

    private final boolean refreshed;

    public ResourceEvent(Object source, boolean refreshed) {
        super(source);
        this.refreshed = refreshed;
    }

    public boolean isRefreshed() { return refreshed; }

    @Override
    public String toString() {
        return source.getClass().toString();
    }
}
