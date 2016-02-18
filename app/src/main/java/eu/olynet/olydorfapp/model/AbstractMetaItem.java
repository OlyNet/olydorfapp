package eu.olynet.olydorfapp.model;

import java.util.Comparator;
import java.util.Date;

/**
 * The abstract base class containing the metadata of corresponding AbstractItems.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public abstract class AbstractMetaItem<T extends AbstractMetaItem<T>> implements Comparable<T> {

    private int id;
    protected Date date = null;
    protected Date lastUpdated = null;
    protected Date lastUsed = null;

    /**
     * Dummy-constructor for (de-)serialization. <b>Do not use!</b>
     */
    public AbstractMetaItem() {

    }

    /**
     * Dummy-constructor for filtering by lastUsed
     *
     * @param lastUsed the Date this item was last used.
     */
    public AbstractMetaItem(Date lastUsed) {
        this.id = -1;
        this.date = null;
        this.lastUpdated = null;
        this.lastUsed = lastUsed;
    }

    /**
     * Dummy-constructor for filtering by id
     *
     * @param id
     */
    public AbstractMetaItem(int id) {
        this.id = id;
        this.date = null;
        this.lastUpdated = null;
        this.lastUsed = null;
    }

    protected AbstractMetaItem(int id, Date date, Date updated) {
        this.id = id;
        this.date = date;
        this.lastUpdated = updated;
        this.lastUsed = null;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public void setLastUsed() {
        this.lastUsed = new Date();
    }

    public void updateItem(T updatedItem) throws ItemMismatchException {
        if (this.equals(updatedItem)) {
            this.date = updatedItem.date;
            this.lastUpdated = updatedItem.lastUpdated;
        } else {
            throw new ItemMismatchException(this.toString() + "cannot be overwritten by " + updatedItem.toString());
        }
    }

    @Override
    public abstract boolean equals(Object obj);

    public String toString() {
        String result = super.toString() + "\n";
        result += "id = " + this.getId() + "\n";
        result += "date = " + this.date + "\n";
        result += "lastUpdated = " + this.lastUpdated + "\n";
        result += "lastUsed = " + this.lastUsed;

        return result;
    }

    @Override
    public int compareTo(T another) {
        if (this.getId() < another.getId())
            return -1;
        else if (this.getId() == another.getId())
            return 0;
        else
            return 1;
    }

    /**
     * Comparator used to order items by their lastUsed date. Needed for periodic cache cleanup.
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
            } else if (lhs.lastUsed == null && rhs.lastUsed == null) {
                return 0;
            } else if (lhs.lastUsed != null && rhs.lastUsed == null) {
                return 1;
            } else if (lhs.lastUsed == null && rhs.lastUsed != null) {
                return -1;
            } else {
                return lhs.lastUsed.compareTo(rhs.lastUsed);
            }
        }
    }
}
