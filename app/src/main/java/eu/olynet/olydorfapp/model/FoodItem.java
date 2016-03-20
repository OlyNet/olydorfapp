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
public class FoodItem extends FoodMetaItem {

    @JsonProperty("organization")
    @JsonDeserialize(using = OrganizationDeserializer.class)
    @JsonSerialize(using = OrganizationSerializer.class)
    protected OrganizationItem organization;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("englishname")
    protected String englishname;

    @JsonProperty("price")
    protected float price;

    @JsonProperty("vegetarian")
    protected boolean vegetarian;

    @JsonProperty("image")
    protected byte[] image;

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public FoodItem() {
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
                    Date date, OrganizationItem organization, String name, String englishname,
                    float price, boolean vegetarian, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date);
        this.organization = organization;
        this.name = name;
        this.englishname = englishname;
        this.price = price;
        this.vegetarian = vegetarian;
        this.image = image;
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
