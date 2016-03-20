/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DailyMealMetaItem extends AbstractMetaItem<DailyMealMetaItem> {

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public DailyMealMetaItem() {
        super();
    }

    public DailyMealMetaItem(Date lastUsed) {
        super(lastUsed);
    }

    public DailyMealMetaItem(int id) {
        super(id);
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationMetaItem to be copied.
     */
    public DailyMealMetaItem(DailyMealMetaItem item) {
        super(item);
    }

    public DailyMealMetaItem(int id, Date createDate, Date editDate, String createUser,
                             String editUser, Date date) {
        super(id, createDate, editDate, createUser, editUser, date);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof DailyMealMetaItem))
            return false;
        DailyMealMetaItem item = (DailyMealMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(DailyMealMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
