/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class OrganizationMetaItem extends AbstractMetaItem<OrganizationMetaItem> {

    public OrganizationMetaItem(Date lastUsed) {
        super(lastUsed);
    }

    public OrganizationMetaItem(int id) {
        super(id);
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationMetaItem to be copied.
     */
    public OrganizationMetaItem(OrganizationMetaItem item) {
        super(item);
    }

    @JsonCreator
    public OrganizationMetaItem(@JsonProperty("id") int id,
                                @JsonProperty("createDate") Date createDate,
                                @JsonProperty("editDate") Date editDate,
                                @JsonProperty("published") boolean published,
                                @JsonProperty("deleted") boolean deleted,
                                @JsonProperty("createUser") String createUser,
                                @JsonProperty("editUser") String editUser) {
        super(id, createDate, editDate, published, deleted, createUser, editUser);
    }

    @Override
    public String toString() {
        String result = super.toString() + "";

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof OrganizationMetaItem))
            return false;
        OrganizationMetaItem item = (OrganizationMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(OrganizationMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
