/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
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
public class NewsItem extends NewsMetaItem {

    /**
     * CREATOR necessary for the Parcelable interface.
     */
    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {

        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @JsonProperty("title") protected String title;

    @JsonProperty("text") protected String text;

    @JsonDeserialize(using = ImageDeserializer.class)
    @JsonProperty("image") protected byte[] image;

    /**
     * Constructor for creating NewsItem from Parcels.
     *
     * @param in the Parcel this NewsItem is to be created from.
     */
    protected NewsItem(Parcel in) {
        super(in);
        this.title = in.readString();
        this.text = in.readString();

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
    protected NewsItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the NewsItem to be copied.
     */
    public NewsItem(NewsItem item) {
        super(item);
        this.title = item.title;
        this.text = item.text;
        this.image = item.image;
    }

    public NewsItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                    Date date, String link, Date lastUsedDate, OrganizationItem organization,
                    String title, String text, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, link, organization,
              lastUsedDate);
        this.title = title;
        this.text = text;
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
        dest.writeString(title);
        dest.writeString(text);

        int imageLength = (image != null ? image.length : -1);
        dest.writeInt(imageLength);
        if (imageLength <= 0) {
            dest.writeByteArray(new byte[0]);
        } else {
            dest.writeByteArray(image);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String content) {
        this.text = content;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(NewsItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.title = updatedItem.title;
        this.text = updatedItem.text;
        this.image = updatedItem.image;
    }

    @Override
    public boolean exactlyEquals(AbstractMetaItem<?> another) {
        return (super.exactlyEquals(another) &&
                this.title.equals(((NewsItem) another).title) &&
                this.text.equals(((NewsItem) another).text) &&
                Arrays.equals(this.image, ((NewsItem) another).image));
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "title = " + this.title + "\n";
        result += "text = " + this.text + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
