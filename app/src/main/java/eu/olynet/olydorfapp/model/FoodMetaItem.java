/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class FoodMetaItem extends AbstractMetaItem<FoodMetaItem> {

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public FoodMetaItem() {
        super();
    }

    public FoodMetaItem(Date lastUsed) {
        super(lastUsed);
    }

    public FoodMetaItem(int id) {
        super(id);
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the FoodMetaItem to be copied.
     */
    public FoodMetaItem(FoodMetaItem item) {
        super(item);
    }

    public FoodMetaItem(int id, Date createDate, Date editDate, boolean published, boolean deleted,
                        String createUser, String editUser, Date date) {
        super(id, createDate, editDate, published, deleted, createUser, editUser, date);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof FoodMetaItem))
            return false;
        FoodMetaItem item = (FoodMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void updateItem(FoodMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
