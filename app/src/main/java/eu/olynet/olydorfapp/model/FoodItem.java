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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class FoodItem extends FoodMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<FoodItem> CREATOR = new Parcelable.Creator<FoodItem>() {

        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    @JsonProperty("name") protected String name;

    @JsonProperty("englishname") protected String englishname;

    @JsonProperty("vegetarian") protected boolean vegetarian;

    @JsonProperty("price") protected float price;

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image") protected byte[] image;

    /**
     * Constructor for creating FoodItem from Parcels.
     *
     * @param in the Parcel this FoodItem is to be created from.
     */
    protected FoodItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.englishname = in.readString();
        this.vegetarian = in.readByte() != 0; /* byte -> boolean */
        this.price = in.readFloat();

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
    protected FoodItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the FoodMetaItem to be copied.
     */
    public FoodItem(FoodItem item) {
        super(item);
        this.name = item.name;
        this.englishname = item.englishname;
        this.price = item.price;
        this.vegetarian = item.vegetarian;
        this.image = item.image;
    }

    public FoodItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                    Date date, String link, int organization, Date lastUsedDate,
                    String name, String englishname, boolean vegetarian, float price,
                    byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
        this.name = name;
        this.englishname = englishname;
        this.price = price;
        this.vegetarian = vegetarian;
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
        dest.writeString(englishname);
        dest.writeByte((byte) (vegetarian ? 1 : 0)); /* boolean -> byte */
        dest.writeFloat(price);

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

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(FoodItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.englishname = updatedItem.englishname;
        this.price = updatedItem.price;
        this.vegetarian = updatedItem.vegetarian;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.name.equals(((FoodItem) another).name) &&
                this.englishname.equals(((FoodItem) another).englishname) &&
                this.vegetarian == ((FoodItem) another).vegetarian &&
                this.price == ((FoodItem) another).price &&
                Arrays.equals(this.image, ((FoodItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "englishname = " + this.englishname + "\n";
        result += "price = " + this.price + "\n";
        result += "vegetarian = " + this.vegetarian + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
