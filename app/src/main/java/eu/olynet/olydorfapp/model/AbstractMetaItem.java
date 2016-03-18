/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import android.support.annotation.NonNull;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Comparator;
import java.util.Date;

/**
 * The abstract base class containing the metadata of corresponding AbstractItems.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public abstract class AbstractMetaItem<T extends AbstractMetaItem<T>> implements Comparable<T> {

    private final int id;

    protected Date createDate;
    protected Date editDate;
    protected Date lastUsedDate;

    protected boolean published;
    protected boolean deleted;

    protected String createUser;
    protected String editUser;

    /**
     * Dummy-constructor for filtering by lastUsedDate
     *
     * @param lastUsedDate the Date this item was last used.
     */
    protected AbstractMetaItem(Date lastUsedDate) {
        this.id = -1;
        this.createDate = null;
        this.editDate = null;
        this.lastUsedDate = lastUsedDate;
        this.published = false;
        this.deleted = false;
        this.createUser = null;
        this.editUser = null;
    }

    /**
     * Dummy-constructor for filtering by ID.
     *
     * @param id the ID of the dummy item.
     */
    protected AbstractMetaItem(int id) {
        this.id = id;
        this.createDate = null;
        this.editDate = null;
        this.lastUsedDate = null;
        this.published = false;
        this.deleted = false;
        this.createUser = null;
        this.editUser = null;
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the AbstractMetaItem to be copied.
     */
    protected AbstractMetaItem(AbstractMetaItem item) {
        this.id = item.id;
        this.createDate = item.createDate;
        this.editDate = item.editDate;
        this.lastUsedDate = item.lastUsedDate;
        this.published = item.published;
        this.deleted = item.deleted;
        this.createUser = item.createUser;
        this.editUser = item.editUser;
    }

    @JsonCreator
    protected AbstractMetaItem(@JsonProperty("id") int id,
                               @JsonProperty("createDate") Date createDate,
                               @JsonProperty("editDate") Date editDate,
                               @JsonProperty("published") boolean published,
                               @JsonProperty("deleted") boolean deleted,
                               @JsonProperty("createUser") String createUser,
                               @JsonProperty("editUser") String editUser) {
        this.id = id;
        this.createDate = createDate;
        this.editDate = editDate;
        this.lastUsedDate = new Date(); /* set lastUsedDate to now */
        this.published = published;
        this.deleted = deleted;
        this.createUser = createUser;
        this.editUser = editUser;
    }

    public int getId() {
        return id;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getEditUser() {
        return editUser;
    }

    public void setEditUser(String editUser) {
        this.editUser = editUser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getEditDate() {
        return editDate;
    }

    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    public Date getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(Date lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public void setLastUsedDate() {
        this.lastUsedDate = new Date();
    }

    public void updateItem(T updatedItem) throws ItemMismatchException {
        if (this.equals(updatedItem)) {
            this.createDate = updatedItem.createDate;
            this.editDate = updatedItem.editDate;
            this.lastUsedDate = new Date();
            this.published = updatedItem.published;
            this.deleted = updatedItem.deleted;
            this.createUser = updatedItem.createUser;
            this.editUser = updatedItem.editUser;
        } else {
            throw new ItemMismatchException(this.toString() + "cannot be overwritten by "
                    + updatedItem.toString());
        }
    }

    @Override
    public abstract boolean equals(Object obj);

    public String toString() {
        String result = super.toString() + "\n";
        result += "id = " + this.getId() + "\n";
        result += "createDate = " + this.createDate + "\n";
        result += "editDate = " + this.editDate + "\n";
        result += "lastUsedDate = " + this.lastUsedDate + "\n";
        result += "published = " + this.published + "\n";
        result += "deleted = " + this.deleted + "\n";
        result += "createUser = " + this.createUser + "\n";
        result += "editUser = " + this.editUser;

        return result;
    }

    @Override
    public int compareTo(@NonNull T another) {
        if (this.getId() < another.getId())
            return -1;
        else if (this.getId() == another.getId())
            return 0;
        else
            return 1;
    }

    /**
     * Comparator used to order items by their lastUsedDate createDate. Needed for periodic cache
     * cleanup.
     */
    public static class LastUsedComparator implements Comparator<AbstractMetaItem> {
        @Override
        public int compare(AbstractMetaItem lhs, AbstractMetaItem rhs) {
            if (lhs == null && rhs == null) {
                return 0;
            } else if (lhs == null && rhs != null) {
                return -1;
            } else if (lhs != null && rhs == null) {
                return 1;
            } else if (lhs.lastUsedDate == null && rhs.lastUsedDate == null) {
                return 0;
            } else if (lhs.lastUsedDate != null && rhs.lastUsedDate == null) {
                return 1;
            } else if (lhs.lastUsedDate == null && rhs.lastUsedDate != null) {
                return -1;
            } else {
                return lhs.lastUsedDate.compareTo(rhs.lastUsedDate);
            }
        }
    }
}
