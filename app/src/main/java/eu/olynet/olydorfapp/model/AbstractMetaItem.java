/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Constructor;
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
public abstract class AbstractMetaItem<T extends AbstractMetaItem<T>> implements Comparable<T>,
        Parcelable {

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
    protected AbstractMetaItem() {
        super();
        this.id = -1;
        this.createDate = null;
        this.editDate = null;
        this.createUser = null;
        this.editUser = null;
        this.date = null;
        this.lastUsedDate = null;
    }

    /**
     * Constructor for creating AbstractMetaItem from Parcels.
     *
     * @param in the Parcel this AbstractMetaItem is to be created from.
     */
    protected AbstractMetaItem(Parcel in) {
        this.id = in.readInt();
        this.createDate = new Date(in.readLong()); /* long -> Date */
        this.editDate = new Date(in.readLong()); /* long -> Date */
        this.createUser = in.readString();
        this.editUser = in.readString();
        this.lastUsedDate = new Date(in.readLong()); /* long -> Date */
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
        this.createUser = item.createUser;
        this.editUser = item.editUser;
        this.date = item.date;
        this.lastUsedDate = item.lastUsedDate;
    }

    /**
     * Create a new AbstractMetaItem.
     *
     * @param id           the unique id of this item.
     * @param createDate   the Date this item was created on.
     * @param editDate     the Date this item was last modified on.
     * @param createUser   the user that created the item.
     * @param editUser     the user that last modified this item.
     * @param date         the Date of this item (meaning depends on the subclass).
     * @param lastUsedDate the Date this item was last used on.
     */
    protected AbstractMetaItem(int id, Date createDate, Date editDate, String createUser,
                               String editUser, Date date, Date lastUsedDate) {
        this.id = id;
        this.createDate = createDate;
        this.editDate = editDate;
        this.createUser = createUser;
        this.editUser = editUser;
        this.date = date;
        this.lastUsedDate = lastUsedDate;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(createDate.getTime()); /* Date -> long */
        dest.writeLong(editDate.getTime()); /* Date -> long */
        dest.writeString(createUser);
        dest.writeString(editUser);
        dest.writeLong(lastUsedDate.getTime()); /* Date -> long */
    }

    /**
     * @param id the unique id of this item.
     */
    private void setId(int id) {
        this.id = id;
    }

    /**
     * @return the unique id of this item.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the Date this item was created on.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the Date this item was created on.
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the Date this item was last modified on.
     */
    public Date getEditDate() {
        return editDate;
    }

    /**
     * @param editDate the Date this item was last modified on.
     */
    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    /**
     * @return the user that created the item.
     */
    public String getCreateUser() {
        return createUser;
    }

    /**
     * @param createUser the user that created the item.
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * @return the user that last modified this item.
     */
    public String getEditUser() {
        return editUser;
    }

    /**
     * @param editUser the user that last modified this item.
     */
    public void setEditUser(String editUser) {
        this.editUser = editUser;
    }

    /**
     * @return the Date of this item (meaning depends on the subclass).
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the Date of this item (meaning depends on the subclass).
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the Date this item was last used on.
     */
    public Date getLastUsedDate() {
        return lastUsedDate;
    }

    /**
     * @param lastUsedDate the Date this item was last used on.
     */
    public void setLastUsedDate(Date lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    /**
     * Sets this item's lastUsedDate to now.
     */
    public void setLastUsedDate() {
        this.lastUsedDate = new Date();
    }

    /**
     * Updates this AbstractMetaItem with the contents of another one of the same type and same id.
     * <p/>
     * <b>IMPORTANT:</b> Every class that inherits from AbstractMetaItem MUST override this function
     * and call the upper class one!
     *
     * @param updatedItem the AbstractMetaItem whose information is to be copied into this.
     * @throws ItemMismatchException if the items do not match.
     */
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

    /**
     * Checks whether another AbstractMetaItem is exactly the same (i.e. every field is equal) as
     * this one. The regular equals method only checks the id.
     *
     * @param another the other AbstractMetaItem.
     * @return true if and only if they are exactly equal.
     */
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (this.equals(another)
                && this.id == another.id
                && this.createDate.equals(another.createDate)
                && this.editDate.equals(another.editDate)
                && this.createUser.equals(another.createUser)
                && this.editUser.equals(another.editUser)
                && this.lastUsedDate.equals(another.lastUsedDate));
    }

    @Override
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
    public static class LastUsedComparator implements Comparator<AbstractMetaItem<?>> {
        @Override
        public int compare(AbstractMetaItem<?> lhs, AbstractMetaItem<?> rhs) {
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

    /**
     * A factory for creating dummy AbstractMetaItems for filtering purposes.
     *
     * @param <T> the specific type of item to be created. Must be a subtype of AbstractMetaItem.
     */
    public static class DummyFactory<T extends AbstractMetaItem<T>> {

        private final Class<T> clazz;

        private int id = -1;
        private Date createDate = null;
        private Date editDate = null;
        private String createUser = null;
        private String editUser = null;
        private Date date = null;
        private Date lastUsedDate = null;

        /**
         * Creates a factory for dummy AbstractMetaItems.
         *
         * @param clazz the Class of the AbstractMetaItem to be created.
         */
        public DummyFactory(Class<T> clazz) {
            if (clazz == null) {
                throw new NullPointerException("clazz cannot be null");
            }
            this.clazz = clazz;
        }

        /**
         * Constructs the dummy item that was specified by this factory.
         *
         * @return the dummy item with the specified fields.
         */
        public T build() {
            try {
                Constructor<?> cons = clazz.getConstructor(int.class, Date.class, Date.class,
                        String.class, String.class, Date.class, Date.class);
                return clazz.cast(cons.newInstance(id, createDate, editDate, createUser, editUser,
                        date, lastUsedDate));
            } catch (Exception e) {
                throw new RuntimeException("dynamic construction failed - " + this.clazz, e);
            }
        }

        /**
         * @param id the ID that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setId(int id) {
            this.id = id;
            return this;
        }

        /**
         * @param createDate the createDate that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setCreateDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        /**
         * @param editDate the editDate that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setEditDate(Date editDate) {
            this.editDate = editDate;
            return this;
        }

        /**
         * @param createUser the createUser that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setCreateUser(String createUser) {
            this.createUser = createUser;
            return this;
        }

        /**
         * @param editUser the editUser that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setEditUser(String editUser) {
            this.editUser = editUser;
            return this;
        }

        /**
         * @param date the date that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setDate(Date date) {
            this.date = date;
            return this;
        }

        /**
         * @param lastUsedDate the lastUsedDate that the dummy item should have.
         * @return this factory.
         */
        public DummyFactory<T> setLastUsedDate(Date lastUsedDate) {
            this.lastUsedDate = lastUsedDate;
            return this;
        }
    }
}
