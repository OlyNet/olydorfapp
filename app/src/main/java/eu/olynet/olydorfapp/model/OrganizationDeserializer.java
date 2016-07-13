/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;

import eu.olynet.olydorfapp.resource.ProductionResourceManager;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class OrganizationDeserializer extends JsonDeserializer<OrganizationItem> {

    /**
     * Method that can be called to ask implementation to deserialize
     * JSON content into the value type this serializer handles.
     * Returned instance is to be constructed by method itself.
     * <p>
     * Pre-condition for this method is that the parser points to the
     * first event that is part of value to deserializer (and which
     * is never JSON 'null' literal, more on this below): for simple
     * types it may be the only value; and for structured types the
     * Object start marker or a FIELD_NAME.
     * </p>
     * <p>
     * The two possible input conditions for structured types result
     * from polymorphism via fields. In the ordinary case, Jackson
     * calls this method when it has encountered an OBJECT_START,
     * and the method implementation must advance to the next token to
     * see the first field name. If the application configures
     * polymorphism via a field, then the object looks like the following.
     * <pre>
     *      {
     *          "@class": "class name",
     *          ...
     *      }
     *  </pre>
     * Jackson consumes the two tokens (the <tt>@class</tt> field name
     * and its value) in order to learn the class and select the deserializer.
     * Thus, the stream is pointing to the FIELD_NAME for the first field
     * after the @class. Thus, if you want your method to work correctly
     * both with and without polymorphism, you must begin your method with:
     * <pre>
     *       if (jp.getCurrentToken() == JsonToken.START_OBJECT) {
     *         jp.nextToken();
     *       }
     *  </pre>
     * This results in the stream pointing to the field name, so that
     * the two conditions align.
     * </p>
     * <p/>
     * Post-condition is that the parser will point to the last
     * event that is part of deserialized value (or in case deserialization
     * fails, event that was not recognized or usable, which may be
     * the same event as the one it pointed to upon call).
     * <p/>
     * Note that this method is never called for JSON null literal,
     * and thus deserializers need (and should) not check for it.
     *
     * @param jp   Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return Deserialized value
     */
    @Override
    public OrganizationItem deserialize(JsonParser jp, DeserializationContext ctxt) throws
                                                                                    IOException,
                                                                                    JsonProcessingException {
        ProductionResourceManager rm = ProductionResourceManager.getInstance();

        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) node.get("eu.olynet.dorfapp.server.data.model.Organization")
                               .numberValue();
        if (id <= 0) {
            throw new IOException("id=" + id + " does not refer to a valid OrganizationItem");
        }

        /* assuming up-to-date meta-data */
        OrganizationItem org = (OrganizationItem) rm.getItem(OrganizationMetaItem.class, id);
        if (org == null) {
            throw new JsonParseException(jp, "getting the OrganizationItem lead to a null value");
        } else {
            return org;
        }
    }

    /**
     * Deserialization called when type being deserialized is defined to
     * contain additional type identifier, to allow for correctly
     * instantiating correct subtype. This can be due to annotation on
     * type (or its supertype), or due to global settings without
     * annotations.
     * <p/>
     * Default implementation may work for some types, but ideally subclasses
     * should not rely on current default implementation.
     * Implementation is mostly provided to avoid compilation errors with older
     * code.
     *
     * @param typeDeserializer Deserializer to use for handling type information
     */
    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
                                      TypeDeserializer typeDeserializer) throws IOException {
        return this.deserialize(p, ctxt);
    }
}
