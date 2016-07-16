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
public class FoodMetaItem extends AbstractMetaItem<FoodMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<FoodMetaItem> CREATOR
            = new Parcelable.Creator<FoodMetaItem>() {

        public FoodMetaItem createFromParcel(Parcel in) {
            return new FoodMetaItem(in);
        }

        public FoodMetaItem[] newArray(int size) {
            return new FoodMetaItem[size];
        }
    };

    /**
     * Constructor for creating FoodMetaItem from Parcels.
     *
     * @param in the Parcel this FoodMetaItem is to be created from.
     */
    protected FoodMetaItem(Parcel in) {
        super(in);
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected FoodMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the FoodMetaItem to be copied.
     */
    public FoodMetaItem(FoodMetaItem item) {
        super(item);
    }

    public FoodMetaItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                        Date date, String link, OrganizationItem organization, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FoodMetaItem)) {
            return false;
        }
        FoodMetaItem item = (FoodMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(35323, 848157).append(this.getId()).build();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void updateItem(FoodMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
