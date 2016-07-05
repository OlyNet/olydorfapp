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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({"food"})
public class OrganizationItem extends OrganizationMetaItem {

    @JsonProperty("name")
    protected String name;

    @JsonProperty("shortname")
    protected String shortname;

    @JsonProperty("website")
    protected String website;

    @JsonProperty("description")
    protected String description;

    @JsonProperty("logo")
    protected byte[] logo;

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<OrganizationItem> CREATOR =
            new Parcelable.Creator<OrganizationItem>() {

                public OrganizationItem createFromParcel(Parcel in) {
                    return new OrganizationItem(in);
                }

                public OrganizationItem[] newArray(int size) {
                    return new OrganizationItem[size];
                }
            };

    /**
     * Constructor for creating OrganizationItem from Parcels.
     *
     * @param in the Parcel this OrganizationItem is to be created from.
     */
    protected OrganizationItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.shortname = in.readString();
        this.website = in.readString();
        this.description = in.readString();

        int imageLength = in.readInt();
        if (imageLength < 0) {
            in.readByteArray(new byte[0]);
            this.logo = null;
        } else {
            this.logo = new byte[imageLength];
            in.readByteArray(this.logo);
        }
    }

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected OrganizationItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationItem to be copied.
     */
    public OrganizationItem(OrganizationItem item) {
        super(item);
        this.name = item.name;
        this.shortname = item.shortname;
        this.website = item.website;
        this.description = item.description;
        this.logo = item.logo;
    }

    public OrganizationItem(int id, Date createDate, Date editDate, String createUser,
                            String editUser, Date date, String link, Date lastUsedDate, String name,
                            String shortname, String website, String description, byte[] logo) {
        super(id, createDate, editDate, createUser, editUser, date, link, lastUsedDate);
        this.name = name;
        this.shortname = shortname;
        this.website = website;
        this.description = description;
        this.logo = logo;
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
        dest.writeString(shortname);
        dest.writeString(website);
        dest.writeString(description);

        int imageLength = (logo != null ? logo.length : -1);
        dest.writeInt(imageLength);
        if (imageLength <= 0) {
            dest.writeByteArray(new byte[0]);
        } else {
            dest.writeByteArray(logo);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public void updateItem(OrganizationItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.shortname = updatedItem.shortname;
        this.website = updatedItem.website;
        this.description = updatedItem.description;
        this.logo = updatedItem.logo;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another)
                && this.name.equals(((OrganizationItem) another).name)
                && this.shortname.equals(((OrganizationItem) another).shortname)
                && this.website.equals(((OrganizationItem) another).website)
                && this.description.equals(((OrganizationItem) another).description)
                && Arrays.equals(this.logo, ((OrganizationItem) another).logo)
        );
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "shortname = " + this.shortname + "\n";
        result += "website = " + this.website + "\n";
        result += "description = " + this.description + "\n";
        result += "image = " + ((logo != null) ? logo.length : 0) + " Byte";

        return result;
    }
}
