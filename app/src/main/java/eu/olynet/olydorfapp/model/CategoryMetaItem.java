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
public class CategoryMetaItem extends AbstractMetaItem<CategoryMetaItem> {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Creator<CategoryMetaItem> CREATOR
            = new Creator<CategoryMetaItem>() {

        public CategoryMetaItem createFromParcel(Parcel in) {
            return new CategoryMetaItem(in);
        }

        public CategoryMetaItem[] newArray(int size) {
            return new CategoryMetaItem[size];
        }
    };

    /**
     * Constructor for creating CategoryMetaItem from Parcels.
     *
     * @param in the Parcel this CategoryMetaItem is to be created from.
     */
    protected CategoryMetaItem(Parcel in) {
        super(in);
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected CategoryMetaItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the CategoryMetaItem to be copied.
     */
    public CategoryMetaItem(CategoryMetaItem item) {
        super(item);
    }

    public CategoryMetaItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                            Date date, String link, int organization, Date lastUsedDate) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CategoryMetaItem)) {
            return false;
        }
        CategoryMetaItem item = (CategoryMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(35929123, 8236541).append(this.getId()).build();
    }

    @Override
    public void updateItem(CategoryMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
    }
}
