package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * The abstract base class containing the metadata of corresponding AbstractItems.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public abstract class AbstractMetaItem<T extends AbstractMetaItem<T>> implements Comparable<T> {

    private final long id;
    protected Date date;
    protected Date lastUpdated;
    protected Date lastUsed = null;

    protected AbstractMetaItem(long id, Date date, Date updated) {
        this.id = id;
        this.date = date;
        this.lastUpdated = updated;
    }

    public long getId() {
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

    protected void setLastUsed(Date lastUsed) {
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
}
