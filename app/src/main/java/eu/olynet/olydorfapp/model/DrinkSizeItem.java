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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class DrinkSizeItem extends DrinkSizeMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Creator<DrinkSizeItem> CREATOR = new Creator<DrinkSizeItem>() {

        public DrinkSizeItem createFromParcel(Parcel in) {
            return new DrinkSizeItem(in);
        }

        public DrinkSizeItem[] newArray(int size) {
            return new DrinkSizeItem[size];
        }
    };

    @JsonProperty("price") protected float price;

    @JsonProperty("size") protected float size;

    @JsonProperty("drink") protected int drink;

    /**
     * Constructor for creating DrinkSizeItem from Parcels.
     *
     * @param in the Parcel this DrinkSizeItem is to be created from.
     */
    protected DrinkSizeItem(Parcel in) {
        super(in);
        this.price = in.readFloat();
        this.size = in.readFloat();
        this.drink = in.readInt();
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected DrinkSizeItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the DrinkSizeItem to be copied.
     */
    public DrinkSizeItem(DrinkSizeItem item) {
        super(item);
        this.price = item.price;
        this.size = item.size;
        this.drink = item.drink;
    }

    public DrinkSizeItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                         Date date, String link, int organization, Date lastUsedDate,
                         float price, float size, int drink) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
        this.price = price;
        this.size = size;
        this.drink = drink;
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
        dest.writeFloat(price);
        dest.writeFloat(size);
        dest.writeInt(drink);
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getDrink() {
        return drink;
    }

    public void setDrink(int drink) {
        this.drink = drink;
    }

    public void updateItem(DrinkSizeItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.price = updatedItem.price;
        this.size = updatedItem.size;
        this.drink = updatedItem.drink;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.price == ((DrinkSizeItem) another).price &&
                this.size == ((DrinkSizeItem) another).size &&
                this.drink == ((DrinkSizeItem) another).drink);
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "price = " + this.price + "\n";
        result += "size = " + this.size + "\n";
        result += "drink = " + this.drink;

        return result;
    }
}
