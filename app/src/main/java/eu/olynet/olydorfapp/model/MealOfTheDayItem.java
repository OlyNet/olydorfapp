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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties("mealOfTheDayRatings")
@SuppressWarnings("unused")
public class MealOfTheDayItem extends MealOfTheDayMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<MealOfTheDayItem> CREATOR
            = new Parcelable.Creator<MealOfTheDayItem>() {

        public MealOfTheDayItem createFromParcel(Parcel in) {
            return new MealOfTheDayItem(in);
        }

        public MealOfTheDayItem[] newArray(int size) {
            return new MealOfTheDayItem[size];
        }
    };

    @JsonProperty("cook")
    protected String cook;

    @JsonProperty("price")
    protected float price;

    @JsonProperty("dailyMeal")
    protected int dailyMeal;

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image")
    protected byte[] image;

    /**
     * Constructor for creating MealOfTheDayItem from Parcels.
     *
     * @param in the Parcel this MealOfTheDayItem is to be created from.
     */
    protected MealOfTheDayItem(Parcel in) {
        super(in);
        this.cook = in.readString();
        this.price = in.readFloat();
        this.dailyMeal = in.readInt();

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
    protected MealOfTheDayItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the MealOfTheDayItem to be copied.
     */
    public MealOfTheDayItem(MealOfTheDayItem item) {
        super(item);
        this.cook = item.cook;
        this.price = item.price;
        this.dailyMeal = item.dailyMeal;
        this.image = item.image;
    }

    public MealOfTheDayItem(int id, Date createDate, Date editDate, String createUser,
                            String editUser, Date date, String link, Date lastUsedDate, String cook,
                            float price, int dailyMeal, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, -1, lastUsedDate);
        this.cook = cook;
        this.price = price;
        this.dailyMeal = dailyMeal;
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
        dest.writeString(this.cook);
        dest.writeFloat(this.price);
        dest.writeInt(this.dailyMeal);

        int imageLength = (this.image != null ? this.image.length : -1);
        dest.writeInt(imageLength);
        if (imageLength <= 0) {
            dest.writeByteArray(new byte[0]);
        } else {
            dest.writeByteArray(this.image);
        }
    }

    public String getCook() {
        return cook;
    }

    public void setCook(String cook) {
        this.cook = cook;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDailyMeal() {
        return dailyMeal;
    }

    public void setDailyMeal(int dailyMeal) {
        this.dailyMeal = dailyMeal;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(MealOfTheDayItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.cook = updatedItem.cook;
        this.price = updatedItem.price;
        this.dailyMeal = updatedItem.dailyMeal;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.cook.equals(((MealOfTheDayItem) another).cook) &&
                this.price == ((MealOfTheDayItem) another).price &&
                this.dailyMeal == ((MealOfTheDayItem) another).dailyMeal &&
                Arrays.equals(this.image, ((MealOfTheDayItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "cook = " + this.cook + "\n";
        result += "price = " + this.price + "\n";
        result += "dailyMeal = " + this.dailyMeal + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
