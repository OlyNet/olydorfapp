/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
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
public class OrganizationItem extends OrganizationMetaItem {

    String name;
    String shortname;
    String website;
    String description;
    byte[] logo;

    @JsonCreator
    public OrganizationItem(@JsonProperty("id") int id,
                            @JsonProperty("createDate") Date createDate,
                            @JsonProperty("editDate") Date editDate,
                            @JsonProperty("published") boolean published,
                            @JsonProperty("deleted") boolean deleted,
                            @JsonProperty("createUser") String createUser,
                            @JsonProperty("editUser") String editUser,
                            @JsonProperty("name") String name,
                            @JsonProperty("shortname") String shortname,
                            @JsonProperty("website") String website,
                            @JsonProperty("description") String description,
                            @JsonProperty("logo") byte[] logo) {
        super(id, createDate, editDate, published, deleted, createUser, editUser);
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
        result += "logo = " + ((logo != null) ? logo.length : 0) + " Byte";

        return result;
    }
}
