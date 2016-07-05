/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class NewsMetaItem extends AbstractMetaItem<NewsMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<NewsMetaItem> CREATOR =
            new Parcelable.Creator<NewsMetaItem>() {

                public NewsMetaItem createFromParcel(Parcel in) {
                    return new NewsMetaItem(in);
                }

                public NewsMetaItem[] newArray(int size) {
                    return new NewsMetaItem[size];
                }
            };

    /**
     * Constructor for creating NewsMetaItems from Parcels.
     *
     * @param in the Parcel this NewsMetaItem is to be created from.
     */
    protected NewsMetaItem(Parcel in) {
        super(in);
    }

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
                        Date date, String link, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, link, lastUsedDate);
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
    public int hashCode() {
        return new HashCodeBuilder(16373, 849455).append(this.getId()).build();
    }

    @Override
    public void updateItem(NewsMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
