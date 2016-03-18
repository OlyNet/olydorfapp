/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NewsItem extends NewsMetaItem {

    protected String text;
    protected byte[] image;

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the NewsItem to be copied.
     */
    public NewsItem(NewsItem item) {
        super(item);
        this.text = item.text;
        this.image = item.image;
    }

    @JsonCreator
    public NewsItem(@JsonProperty("id") int id,
                    @JsonProperty("createDate") Date createDate,
                    @JsonProperty("editDate") Date editDate,
                    @JsonProperty("published") boolean published,
                    @JsonProperty("deleted") boolean deleted,
                    @JsonProperty("createUser") String createUser,
                    @JsonProperty("editUser") String editUser,
                    @JsonProperty("organization") OrganizationItem organization,
                    @JsonProperty("date") Date date,
                    @JsonProperty("title") String title,
                    @JsonProperty("link") String link,
                    @JsonProperty("text") String text,
                    @JsonProperty("image") byte[] image) {
        super(id, createDate, editDate, published, deleted, createUser, editUser, organization,
                date, title, link);
        this.text = text;
        this.image = image;
    }

    public String getText() {
        this.setLastUsedDate();
        return text;
    }

    public void setText(String content) {
        this.text = content;
    }

    public byte[] getImage() {
        this.setLastUsedDate();
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(NewsItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.text = updatedItem.text;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "text = " + this.text + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
