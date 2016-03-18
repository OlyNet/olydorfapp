/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

import eu.olynet.olydorfapp.resources.OrganizationDeserializer;
import eu.olynet.olydorfapp.resources.OrganizationSerializer;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class FoodMetaItem extends AbstractMetaItem<FoodMetaItem> {

    @JsonDeserialize(using = OrganizationDeserializer.class)
    @JsonSerialize(using = OrganizationSerializer.class)
    protected OrganizationItem organization = null;

    protected String name;
    protected String englishname;
    protected float price;
    protected boolean vegetarian;

    public FoodMetaItem(Date lastUsed) {
        super(lastUsed);
        this.organization = null;
        this.name = null;
        this.englishname = null;
        this.price = Float.NaN;
        this.vegetarian = false;
    }

    public FoodMetaItem(int id) {
        super(id);
        this.organization = null;
        this.name = null;
        this.englishname = null;
        this.price = Float.NaN;
        this.vegetarian = false;
    }

    @JsonCreator
    public FoodMetaItem(@JsonProperty("id") int id,
                        @JsonProperty("createDate") Date createDate,
                        @JsonProperty("editDate") Date editDate,
                        @JsonProperty("published") boolean published,
                        @JsonProperty("deleted") boolean deleted,
                        @JsonProperty("createUser") String createUser,
                        @JsonProperty("editUser") String editUser,
                        @JsonProperty("organization") OrganizationItem organization,
                        @JsonProperty("name") String name,
                        @JsonProperty("englishname") String englishname,
                        @JsonProperty("price") float price,
                        @JsonProperty("vegetarian") boolean vegetarian) {
        super(id, createDate, editDate, published, deleted, createUser, editUser);
        this.organization = organization;
        this.name = name;
        this.englishname = englishname;
        this.price = price;
        this.vegetarian = vegetarian;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof FoodMetaItem))
            return false;
        FoodMetaItem item = (FoodMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "organization = [[" + this.organization.toString() + "]]" + "\n";
        result += "name = " + this.name + "\n";
        result += "englishname = " + this.englishname + "\n";
        result += "price = " + this.price + "\n";
        result += "vegetarian = " + this.vegetarian;

        return result;
    }

    @Override
    public void updateItem(FoodMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.organization = updatedItem.organization;
        this.name = updatedItem.name;
        this.englishname = updatedItem.englishname;
        this.price = updatedItem.price;
        this.vegetarian = updatedItem.vegetarian;
    }
}
