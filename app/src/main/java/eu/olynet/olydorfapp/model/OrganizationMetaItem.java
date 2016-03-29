/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OrganizationMetaItem extends AbstractMetaItem<OrganizationMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<OrganizationMetaItem> CREATOR =
            new Parcelable.Creator<OrganizationMetaItem>() {

                public OrganizationMetaItem createFromParcel(Parcel in) {
                    return new OrganizationMetaItem(in);
                }

                public OrganizationMetaItem[] newArray(int size) {
                    return new OrganizationMetaItem[size];
                }
            };

    /**
     * Constructor for creating OrganizationMetaItem from Parcels.
     *
     * @param in the Parcel this OrganizationMetaItem is to be created from.
     */
    protected OrganizationMetaItem(Parcel in) {
        super(in);
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected OrganizationMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationMetaItem to be copied.
     */
    public OrganizationMetaItem(OrganizationMetaItem item) {
        super(item);
    }

    public OrganizationMetaItem(int id, Date createDate, Date editDate, String createUser,
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
        if (!(obj instanceof OrganizationMetaItem))
            return false;
        OrganizationMetaItem item = (OrganizationMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(OrganizationMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
