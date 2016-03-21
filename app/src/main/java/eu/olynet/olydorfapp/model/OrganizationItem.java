/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
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
                            String editUser, Date date, Date lastUsedDate, String name,
                            String shortname, String website, String description, byte[] logo) {
        super(id, createDate, editDate, createUser, editUser, date, lastUsedDate);
        this.name = name;
        this.shortname = shortname;
        this.website = website;
        this.description = description;
        this.logo = logo;
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
