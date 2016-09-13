/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationMetaItem extends AbstractMetaItem<OrganizationMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<OrganizationMetaItem> CREATOR
            = new Parcelable.Creator<OrganizationMetaItem>() {

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
                                String editUser, Date date, String link,
                                int organization, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OrganizationMetaItem)) {
            return false;
        }
        OrganizationMetaItem item = (OrganizationMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(356714653, 86897057).append(this.getId()).build();
    }

    @Override
    public void updateItem(OrganizationMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
