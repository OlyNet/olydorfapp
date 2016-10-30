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
public class DrinkSizeMetaItem extends AbstractMetaItem<DrinkSizeMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Creator<DrinkSizeMetaItem> CREATOR
            = new Creator<DrinkSizeMetaItem>() {

        public DrinkSizeMetaItem createFromParcel(Parcel in) {
            return new DrinkSizeMetaItem(in);
        }

        public DrinkSizeMetaItem[] newArray(int size) {
            return new DrinkSizeMetaItem[size];
        }
    };

    /**
     * Constructor for creating DrinkSizeMetaItem from Parcels.
     *
     * @param in the Parcel this DrinkSizeMetaItem is to be created from.
     */
    protected DrinkSizeMetaItem(Parcel in) {
        super(in);
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected DrinkSizeMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the DrinkSizeMetaItem to be copied.
     */
    public DrinkSizeMetaItem(DrinkSizeMetaItem item) {
        super(item);
    }

    public DrinkSizeMetaItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                             Date date, String link, int organization, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DrinkSizeMetaItem)) {
            return false;
        }
        DrinkSizeMetaItem item = (DrinkSizeMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(35913, 822547).append(this.getId()).build();
    }

    @Override
    public void updateItem(DrinkSizeMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
