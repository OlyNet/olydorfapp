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
@JsonIgnoreProperties({"food"})
@SuppressWarnings("unused")
public class OrganizationItem extends OrganizationMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<OrganizationItem> CREATOR
            = new Parcelable.Creator<OrganizationItem>() {

        public OrganizationItem createFromParcel(Parcel in) {
            return new OrganizationItem(in);
        }

        public OrganizationItem[] newArray(int size) {
            return new OrganizationItem[size];
        }
    };

    @JsonProperty("name") protected String name;

    @JsonProperty("shortname") protected String shortname;

    @JsonProperty("description") protected String description;

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image") protected byte[] image;

    /**
     * Constructor for creating OrganizationItem from Parcels.
     *
     * @param in the Parcel this OrganizationItem is to be created from.
     */
    protected OrganizationItem(Parcel in) {
        super(in);
        this.name = in.readString();
        this.shortname = in.readString();
        this.description = in.readString();

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
        this.description = item.description;
        this.image = item.image;
    }

    public OrganizationItem(int id, Date createDate, Date editDate, String createUser,
                            String editUser, Date date, String link, Date lastUsedDate, String name,
                            String shortname, String description, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, -1, lastUsedDate);
        this.name = name;
        this.shortname = shortname;
        this.description = description;
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
        dest.writeString(shortname);
        dest.writeString(description);

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

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(OrganizationItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.shortname = updatedItem.shortname;
        this.description = updatedItem.description;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.name.equals(((OrganizationItem) another).name) &&
                this.shortname.equals(((OrganizationItem) another).shortname) &&
                this.description.equals(((OrganizationItem) another).description) &&
                Arrays.equals(this.image, ((OrganizationItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "name = " + this.name + "\n";
        result += "shortname = " + this.shortname + "\n";
        result += "description = " + this.description + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
