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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincentbrison.openlibraries.android.dualcache.lib.Serializer;

import java.io.IOException;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.model.AbstractMetaItemMixIn;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
class CacheSerializer<T> implements Serializer<T> {

    private static final ObjectMapper sMapper;

    static {
        sMapper = new ObjectMapper();
        sMapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        sMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        sMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        sMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        sMapper.addMixIn(AbstractMetaItem.class, AbstractMetaItemMixIn.class);
    }

    private final Class<T> clazz;

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
            return sMapper.readValue(data, this.clazz);
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
