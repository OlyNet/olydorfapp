package eu.olynet.olydorfapp.resource;

import android.os.AsyncTask;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martin on 25.03.2015.
 */
class ResourceTask extends AsyncTask<Class, Void, List<ResourceObject>> {

    private final boolean forceRefresh;
    private final ResourceManager resourceManager;

    public ResourceTask(boolean forceRefresh) {
        resourceManager = ResourceManager.getInstance();
        this.forceRefresh = forceRefresh;
    }

    @Override
    protected List<ResourceObject> doInBackground(Class... classes) {
        List<ResourceObject> list = new LinkedList<>();
        for(int i = 0; i < classes.length; i++) {
            list.add(resourceManager.getResource(classes[i], forceRefresh));
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<ResourceObject> list) {
        for(ResourceObject rs : list) {
            if(rs == null) { continue; }

            ResourceEvent re = new ResourceEvent(rs.resource, rs.refreshed);
            resourceManager.notifyResourceListeners(re);
        }
    }
}
