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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class CategoryItem extends CategoryMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Creator<CategoryItem> CREATOR
            = new Creator<CategoryItem>() {

        public CategoryItem createFromParcel(Parcel in) {
            return new CategoryItem(in);
        }

        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };

    @JsonProperty("name") protected String name;

    @JsonProperty("order") protected int order;

    /**
     * Constructor for creating CategoryItem from Parcels.
     *
     * @param in the Parcel this CategoryItem is to be created from.
     */
    protected CategoryItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.order = in.readInt();
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected CategoryItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the CategoryItem to be copied.
     */
    public CategoryItem(CategoryItem item) {
        super(item);
        this.name = item.name;
        this.order = item.order;
    }

    public CategoryItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                        Date date, String link, Date lastUsedDate, String name, int order) {
        super(id, createDate, editDate, createUser, editUser, date, link, -1, lastUsedDate);
        this.name = name;
        this.order = order;
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
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeInt(order);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void updateItem(CategoryItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.order = updatedItem.order;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.name.equals(((CategoryItem) another).name) &&
                this.order == ((CategoryItem) another).order);
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "order = " + this.order;

        return result;
    }
}
