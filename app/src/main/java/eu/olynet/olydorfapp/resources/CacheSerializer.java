/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.resources;

import com.vincentbrison.openlibraries.android.dualcache.lib.Serializer;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class CacheSerializer<T> implements Serializer<T> {

    private final Class<T> clazz;

    private static final ObjectMapper sMapper;

    static {
        sMapper = new ObjectMapper();
        sMapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.NONE);
        sMapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        sMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    public CacheSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Deserialization of a String into an object.
     *
     * @param data is the string representing the serialized data.
     * @return the deserialized data.
     */
    @Override
    public T fromString(String data) {
        try {
            return sMapper.reader(this.clazz).readValue(data);
        } catch (IOException e) {
            throw new RuntimeException("deserialization failed", e);
        }
    }

    /**
     * Serialization of an object into String.
     *
     * @param object is the object to serialize.
     * @return the result of the serialization into a String.
     */
    @Override
    public String toString(T object) {
        try {
            return sMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }
}
