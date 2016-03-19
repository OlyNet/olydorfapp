/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class OrganizationMetaItem extends AbstractMetaItem<OrganizationMetaItem> {

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public OrganizationMetaItem() {
        super();
    }

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

    public OrganizationMetaItem(int id, Date createDate, Date editDate, boolean published,
                                boolean deleted, String createUser, String editUser, Date date) {
        super(id, createDate, editDate, published, deleted, createUser, editUser, date);
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
