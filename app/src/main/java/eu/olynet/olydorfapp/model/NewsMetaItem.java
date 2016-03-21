/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
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
public class NewsMetaItem extends AbstractMetaItem<NewsMetaItem> {

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected NewsMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the NewsMetaItem to be copied.
     */
    public NewsMetaItem(NewsMetaItem item) {
        super(item);
    }

    public NewsMetaItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                        Date date, Date lastUsedDate) {
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
        if (!(obj instanceof NewsMetaItem))
            return false;
        NewsMetaItem item = (NewsMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(NewsMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
