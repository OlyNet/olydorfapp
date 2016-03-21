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
public class MealOfTheDayMetaItem extends AbstractMetaItem<MealOfTheDayMetaItem> {

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected MealOfTheDayMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationMetaItem to be copied.
     */
    public MealOfTheDayMetaItem(MealOfTheDayMetaItem item) {
        super(item);
    }

    public MealOfTheDayMetaItem(int id, Date createDate, Date editDate, String createUser,
                                String editUser, Date date, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, lastUsedDate);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof MealOfTheDayMetaItem))
            return false;
        MealOfTheDayMetaItem item = (MealOfTheDayMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(MealOfTheDayMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
