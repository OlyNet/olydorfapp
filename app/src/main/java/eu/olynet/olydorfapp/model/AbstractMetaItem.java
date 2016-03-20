/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Comparator;
import java.util.Date;

/**
 * The abstract base class containing the metadata of corresponding AbstractItems.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@SuppressWarnings("unused")
public abstract class AbstractMetaItem<T extends AbstractMetaItem<T>> implements Comparable<T> {

    @JsonProperty("id")
    private int id;

    @JsonProperty("createDate")
    protected Date createDate;

    @JsonProperty("editDate")
    protected Date editDate;

    @JsonProperty("createUser")
    protected String createUser;

    @JsonProperty("editUser")
    protected String editUser;

    @JsonProperty("date")
    protected Date date = null;

    protected Date lastUsedDate = new Date();

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public AbstractMetaItem() {
        super();
    }

    protected AbstractMetaItem(Date date, Void nope) {
        this.id = -1;
        this.createDate = null;
        this.editDate = null;
        this.lastUsedDate = null;
        this.createUser = null;
        this.editUser = null;
        this.date = date;
    }

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
        this.createUser = null;
        this.editUser = null;
        this.date = null;
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
        this.createUser = null;
        this.editUser = null;
        this.date = null;
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
        this.createUser = item.createUser;
        this.editUser = item.editUser;
        this.date = item.date;
    }

    protected AbstractMetaItem(int id, Date createDate, Date editDate, String createUser,
                               String editUser, Date date) {
        this.id = id;
        this.createDate = createDate;
        this.editDate = editDate;
        this.lastUsedDate = new Date(); /* set lastUsedDate to now */
        this.createUser = createUser;
        this.editUser = editUser;
        this.date = date;
    }

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void updateItem(T updatedItem) throws ItemMismatchException {
        if (this.equals(updatedItem)) {
            this.createDate = updatedItem.createDate;
            this.editDate = updatedItem.editDate;
            this.lastUsedDate = new Date();
            this.createUser = updatedItem.createUser;
            this.editUser = updatedItem.editUser;
            this.date = updatedItem.date;
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
        result += "createUser = " + this.createUser + "\n";
        result += "editUser = " + this.editUser + "\n";
        result += "date = " + this.date;

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
            } else if (lhs == null) {
                return -1;
            } else if (rhs == null) {
                return 1;
            } else if (lhs.lastUsedDate == null && rhs.lastUsedDate == null) {
                return 0;
            } else if (lhs.lastUsedDate != null && rhs.lastUsedDate == null) {
                return 1;
            } else if (lhs.lastUsedDate == null) {
                return -1;
            } else {
                return lhs.lastUsedDate.compareTo(rhs.lastUsedDate);
            }
        }
    }

    /**
     * Comparator used to order items by their createDate in ascending order. A use case for this
     * would be displaying daily meals for the next month.
     */
    public static class DateAscComparator implements Comparator<AbstractMetaItem<?>> {
        @Override
        public int compare(AbstractMetaItem<?> lhs, AbstractMetaItem<?> rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    }

    /**
     * Comparator used to order items by their createDate in descending order. A use case for this
     * would be displaying news entries.
     */
    public static class DateDescComparator implements Comparator<AbstractMetaItem<?>> {
        @Override
        public int compare(AbstractMetaItem<?> lhs, AbstractMetaItem<?> rhs) {
            return -lhs.getDate().compareTo(rhs.getDate());
        }
    }
}
