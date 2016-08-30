/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
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
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("mealsofthedays")
@SuppressWarnings("unused")
public class DailyMealItem extends DailyMealMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<DailyMealItem> CREATOR
            = new Parcelable.Creator<DailyMealItem>() {

        public DailyMealItem createFromParcel(Parcel in) {
            return new DailyMealItem(in);
        }

        public DailyMealItem[] newArray(int size) {
            return new DailyMealItem[size];
        }
    };

    @JsonProperty("name") protected String name;

    @JsonProperty("englishName") protected String englishName;

    @JsonProperty("vegetarian") protected boolean vegetarian;

    @JsonProperty("price") protected float price;

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image") protected byte[] image;

    /**
     * Constructor for creating DailyMealItem from Parcels.
     *
     * @param in the Parcel this DailyMealItem is to be created from.
     */
    protected DailyMealItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.englishName = in.readString();
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
    protected DailyMealItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationItem to be copied.
     */
    public DailyMealItem(DailyMealItem item) {
        super(item);
        this.name = item.name;
        this.englishName = item.englishName;
        this.vegetarian = item.vegetarian;
        this.price = item.price;
        this.image = item.image;
    }

    public DailyMealItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                         Date date, String link, Date lastUsedDate, String name, String englishName,
                         boolean vegetarian, float price, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, -1, lastUsedDate);
        this.name = name;
        this.englishName = englishName;
        this.vegetarian = vegetarian;
        this.price = price;
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
        dest.writeString(englishName);
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

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(DailyMealItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.englishName = updatedItem.englishName;
        this.vegetarian = updatedItem.vegetarian;
        this.price = updatedItem.price;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.name.equals(((DailyMealItem) another).name) &&
                this.englishName.equals(((DailyMealItem) another).englishName) &&
                this.vegetarian == ((DailyMealItem) another).vegetarian &&
                this.price == ((DailyMealItem) another).price &&
                Arrays.equals(this.image, ((DailyMealItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "englishName = " + this.englishName + "\n";
        result += "vegetarian = " + this.vegetarian + "\n";
        result += "price = " + this.price + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
