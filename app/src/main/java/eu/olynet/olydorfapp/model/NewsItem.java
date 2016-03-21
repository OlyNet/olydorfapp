/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

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
public class NewsItem extends NewsMetaItem {

    @JsonProperty("organization")
    @JsonSerialize(using = OrganizationSerializer.class)
    @JsonDeserialize(using = OrganizationDeserializer.class)
    protected OrganizationItem organization = null;

    @JsonProperty("title")
    protected String title;

    @JsonProperty("link")
    protected String link;

    @JsonProperty("text")
    protected String text;

    @JsonProperty("image")
    protected byte[] image;

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
        this.organization = item.organization;
        this.title = item.title;
        this.link = item.link;
        this.text = item.text;
        this.image = item.image;
    }

    public NewsItem(int id, Date createDate, Date editDate, String createUser, String editUser,
                    Date date, Date lastUsedDate, OrganizationItem organization, String title,
                    String link, String text, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, lastUsedDate);
        this.organization = organization;
        this.title = title;
        this.link = link;
        this.text = text;
        this.image = image;
    }

    public OrganizationItem getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationItem organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        this.organization = updatedItem.organization;
        this.title = updatedItem.title;
        this.link = updatedItem.link;
        this.text = updatedItem.text;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "organization = [[" + this.organization.toString() + "]]" + "\n";
        result += "title = " + this.title + "\n";
        result += "link = " + this.link + "\n";
        result += "text = " + this.text + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
