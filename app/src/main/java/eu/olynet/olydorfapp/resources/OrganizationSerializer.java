/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.resources;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

import eu.olynet.olydorfapp.model.OrganizationItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class OrganizationSerializer extends JsonSerializer<OrganizationItem>{
    /**
     * Method that can be called to ask implementation to serialize
     * values of type this serializer handles.
     *
     * @param value    Value to serialize; can <b>not</b> be null.
     * @param jgen     Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     */
    @Override
    public void serialize(OrganizationItem value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("eu.olynet.dorfapp.server.data.model.Organization", value.getId());
        jgen.writeEndObject();
    }
}
