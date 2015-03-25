package eu.olynet.olydorfapp.resource;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import eu.olynet.olydorfapp.MealOfTheDay;

/**
 * Created by Martin on 25.03.2015.
 */
public class ResourceManager {

    private final Vector<ResourceListener> listeners;

    private Context context;
    private static ResourceManager instance = null;
    private final WebTarget service;
    private Map<Class, String> classToStringMap;
    private File cacheDir = null;

    private ResourceManager(Context context) {
        this.context = context;
        cacheDir = context.getCacheDir();
        Client client = ClientBuilder.newClient();
        service = client.target("http://ws.olynet.eu"); // TODO move to config file

        /* setup the class to resource string mapping */
        classToStringMap = new LinkedHashMap<Class, String>();
        classToStringMap.put(MealOfTheDay.class, "meal"); // TODO implement and use the real classes

        /* setup the ResourceListener datastructure */
        listeners = new Vector<>(); // TODO implement locking
    }

    public static ResourceManager getInstance() { return instance; }

    public static ResourceManager createInstance(Context context) {
        if(instance == null) {
            instance = new ResourceManager(context);
        }

        return instance;
    }

    public void addResourceListener(ResourceListener listener) {
        listeners.add(listener);
    }

    public boolean removeResourceListener(ResourceListener listener) {
        return listeners.remove(listener);
    }

    public void removeAllResourceListeners() {
        listeners.clear();
    }

    protected void notifyResourceListeners(ResourceEvent re) {
        for(ResourceListener listener : listeners) {
            listener.onResourceEvent(re);
        }
    }

    protected boolean refreshAll() {
        boolean result = true;
        for(Class clazz : classToStringMap.keySet()) {
            result &= refresh(clazz);
        }
        return result;
    }

    protected boolean refresh(Class clazz) {
        String name = classToStringMap.get(clazz);
        if(name == null) {
            throw new IllegalArgumentException("cannot save resource of type "
                    + clazz.toString());
        }

        Object resource = service.path(name).request(MediaType.APPLICATION_JSON).get(clazz);

        return writeCache(resource);
    }

    protected boolean writeCache(Object resource) {
        Class clazz = resource.getClass();

        /* ensure it is a valid Object */
        String name = classToStringMap.get(clazz);
        if(name == null) {
            throw new IllegalArgumentException("cannot save resource of type "
                    + clazz.toString());
        }

        /* write the object to the cache */
        FileOutputStream outputStream;
        try {
            File cacheFile = new File(cacheDir, name);

            outputStream = new FileOutputStream(cacheFile);
            ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
            objectStream.writeObject(resource);
            objectStream.close();
            outputStream.close();
        } catch(Exception e) {
            Log.e("Cache IO", e.toString());
            return false;
        }

        return true;
    }

    protected Object readCache(Class clazz) {
        Object resource = null;

        /* ensure it is a valid Object */
        String name = classToStringMap.get(clazz);
        if(name == null) {
            throw new IllegalArgumentException("cannot save resource of type "
                    + clazz.toString());
        }

        FileInputStream inputStream;
        File cacheFile = new File(cacheDir, name);
        if(cacheFile.exists()) {
            try {
                inputStream = new FileInputStream(cacheFile);
                ObjectInputStream objectStream = new ObjectInputStream(inputStream);
                resource = objectStream.read();
                objectStream.close();
                inputStream.close();
            } catch(Exception e) {
                Log.e("Cache IO", e.toString());
                return null;
            }

            return resource;
        } else {
            return null;
        }
    }

    public ResourceObject getResource(Class clazz, boolean forceRefresh) {
        Object resource = null;

        /* ensure it is a valid Object */
        String name = null;
        if(classToStringMap.containsKey(clazz)) {
            name = classToStringMap.get(clazz);
        } else {
            throw new IllegalArgumentException("cannot fetch resource of type "
                    + clazz.toString());
        }

        /* check the cache */
        File cached = new File(cacheDir, name);
        boolean exists = cached.exists();
        if(!forceRefresh && (exists && cached.lastModified() <
                (System.currentTimeMillis() - 60 * 60 * 1000))) { // older than one hour
            return new ResourceObject(readCache(clazz), false);
        } else {
            try {
                resource = service.path(name).request(MediaType.APPLICATION_JSON).get(clazz);
            } catch(Exception e) {
                Log.e("JAX RS Error", e.toString());
                resource = null;
            }

            if(resource != null) {
                writeCache(resource);
                return new ResourceObject(resource, true); /* got up-to-date resource */
            } else if(forceRefresh) {
                return null; /* only got old resource but up-to-date was requested */
            } else if(exists) {
                return new ResourceObject(readCache(clazz), false); /* got old resource */
            } else {
                return null; /* got nothing */
            }
        }
    }
}

class ResourceObject {

    public final Object resource;
    public final boolean refreshed;

    public ResourceObject(Object resource, boolean refreshed) {
        this.resource = resource;
        this.refreshed = refreshed;
    }

}
