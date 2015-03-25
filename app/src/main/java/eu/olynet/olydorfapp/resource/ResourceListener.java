package eu.olynet.olydorfapp.resource;

import java.util.EventListener;

/**
 * Created by Martin on 25.03.2015.
 */
public interface ResourceListener extends EventListener {

    public void onResourceEvent(ResourceEvent re);

}
