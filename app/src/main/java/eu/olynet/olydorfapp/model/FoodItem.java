/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import eu.olynet.olydorfapp.resources.OrganizationDeserializer;
import eu.olynet.olydorfapp.resources.OrganizationSerializer;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
public class FoodItem extends FoodMetaItem {

    @JsonProperty("organization")
    @JsonDeserialize(using = OrganizationDeserializer.class)
    @JsonSerialize(using = OrganizationSerializer.class)
    protected OrganizationItem organization;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("englishname")
    protected String englishname;

    @JsonProperty("vegetarian")
    protected boolean vegetarian;

    @JsonProperty("price")
    protected float price;

    @JsonProperty("image")
    protected byte[] image;

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<FoodItem> CREATOR =
            new Parcelable.Creator<FoodItem>() {

                public FoodItem createFromParcel(Parcel in) {
                    return new FoodItem(in);
                }

                public FoodItem[] newArray(int size) {
                    return new FoodItem[size];
                }
            };

    /**
     * Constructor for creating FoodItem from Parcels.
     *
     * @param in the Parcel this FoodItem is to be created from.
     */
    protected FoodItem(Parcel in) {
        super(in);
        this.organization = in.readParcelable(OrganizationItem.class.getClassLoader());
        this.name = in.readString();
        this.englishname = in.readString();
        this.vegetarian = in.readByte() != 0; /* byte -> boolean */
        this.price = in.readFloat();

        int imageLength = in.readInt();
        this.image = new byte[imageLength];
        if (imageLength <= 0) {
            in.readByteArray(this.image);
            this.image = null;
        } else {
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
        this.organization = item.organization;
        this.name = item.name;
        this.englishname = item.englishname;
        this.price = item.price;
        this.vegetarian = item.vegetarian;
        this.image = item.image;
    }

    public FoodItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                    Date date, Date lastUsedDate, OrganizationItem organization, String name, String englishname,
                    float price, boolean vegetarian, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, lastUsedDate);
        this.organization = organization;
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
        dest.writeParcelable(this.organization, flags);
        dest.writeString(name);
        dest.writeString(englishname);
        dest.writeByte((byte) (vegetarian ? 1 : 0)); /* boolean -> byte */
        dest.writeFloat(price);

        int imageLength = (image != null ? image.length : 0);
        dest.writeInt(imageLength);
        if (imageLength <= 0) {
            dest.writeByteArray(new byte[0]);
        } else {
            dest.writeByteArray(image);
        }
    }

    public OrganizationItem getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationItem organization) {
        this.organization = organization;
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
        this.organization = updatedItem.organization;
        this.name = updatedItem.name;
        this.englishname = updatedItem.englishname;
        this.price = updatedItem.price;
        this.vegetarian = updatedItem.vegetarian;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "organization = [[" + this.organization.toString() + "]]" + "\n";
        result += "name = " + this.name + "\n";
        result += "englishname = " + this.englishname + "\n";
        result += "price = " + this.price + "\n";
        result += "vegetarian = " + this.vegetarian + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
