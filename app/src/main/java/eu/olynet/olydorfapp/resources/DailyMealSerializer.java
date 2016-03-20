/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;

import eu.olynet.olydorfapp.model.DailyMealItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class DailyMealSerializer extends JsonSerializer<DailyMealItem> {

    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value    Value to serialize; can <b>not</b> be null.
     * @param jgen     Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     */
    @Override
    public void serialize(DailyMealItem value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeNumberField("eu.olynet.dorfapp.server.data.model.DailyMeal", value.getId());
    }

    @Override
    public void serializeWithType(DailyMealItem value, JsonGenerator gen,
                                  SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonProcessingException {
        /*
         * See: https://stackoverflow.com/questions/27876027/json-jackson-exception-when-serializing-a-polymorphic-class-with-custom-serial
         */
        typeSer.writeTypePrefixForObject(value, gen);
        serialize(value, gen, provider);
        typeSer.writeTypeSuffixForObject(value, gen);
    }
}
