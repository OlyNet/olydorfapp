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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class DrinkItem extends DrinkMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Creator<DrinkItem> CREATOR = new Creator<DrinkItem>() {

        public DrinkItem createFromParcel(Parcel in) {
            return new DrinkItem(in);
        }

        public DrinkItem[] newArray(int size) {
            return new DrinkItem[size];
        }
    };

    @JsonProperty("name") protected String name;

    @JsonProperty("category") protected int category;

    @JsonProperty("order") protected int order;

    @JsonProperty("drinkSizes") protected List<DrinkSizeItem> drinkSizes = new ArrayList<>();

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image")
    protected byte[] image;

    /**
     * Constructor for creating DrinkItem from Parcels.
     *
     * @param in the Parcel this DrinkItem is to be created from.
     */
    protected DrinkItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.category = in.readInt();
        this.order = in.readInt();
        in.readTypedList(this.drinkSizes, DrinkSizeItem.CREATOR);

        int imageLength = in.readInt();
        if (imageLength < 0) {
            in.readByteArray(new byte[0]);
            this.image = null;
        } else {
            this.image = new byte[imageLength];
            in.readByteArray(this.image);
        }
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected DrinkItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the DrinkItem to be copied.
     */
    public DrinkItem(DrinkItem item) {
        super(item);
        this.name = item.name;
        this.category = item.category;
        this.order = item.order;
        this.drinkSizes = item.drinkSizes;
        this.image = item.image;
    }

    public DrinkItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                     Date date, String link, int organization, Date lastUsedDate,
                     String name, int category, int order, List<DrinkSizeItem> drinkSizes,
                     byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
        this.name = name;
        this.category = category;
        this.order = order;
        this.drinkSizes = drinkSizes;
        this.image = image;
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
        dest.writeInt(category);
        dest.writeInt(order);
        dest.writeTypedList(drinkSizes);

        int imageLength = (image != null ? image.length : -1);
        dest.writeInt(imageLength);
        if (imageLength <= 0) {
            dest.writeByteArray(new byte[0]);
        } else {
            dest.writeByteArray(image);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<DrinkSizeItem> getDrinkSizes() {
        return drinkSizes;
    }

    public void setDrinkSizes(List<DrinkSizeItem> drinkSizes) {
        this.drinkSizes = drinkSizes;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(DrinkItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.category = updatedItem.category;
        this.order = updatedItem.order;
        this.drinkSizes = updatedItem.drinkSizes;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.name.equals(((DrinkItem) another).name) &&
                this.category == ((DrinkItem) another).category &&
                this.order == ((DrinkItem) another).order &&
                this.drinkSizes.equals(((DrinkItem) another).drinkSizes) &&
                Arrays.equals(this.image, ((DrinkItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "category = " + this.category + "\n";
        result += "order = " + this.order + "\n";
        result += "drinkSizes = " + this.drinkSizes.size() + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
