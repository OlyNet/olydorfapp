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
public class NewsMetaItem extends AbstractMetaItem<NewsMetaItem> {

    protected String title;

    public NewsMetaItem() {
        super();
    }

    public NewsMetaItem(Date lastUsed) {
        super(lastUsed);
        this.title = null;
    }

    public NewsMetaItem(int id) {
        super(id);
        this.title = null;
    }

    public NewsMetaItem(int id, Date date, Date createDate, Date editDate, boolean published,
                        boolean deleted, String createUser, String editUser,
                        Organization organization, String title) {
        super(id, date, createDate, editDate, published, deleted, createUser, editUser,
                organization);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "title = " + this.title + "\n";

        return result;
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
        this.title = updatedItem.title;
    }
}
