/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties("mealsofthedays")
public class DailyMealItem extends DailyMealMetaItem {


    @JsonProperty("organization")
    @JsonSerialize(using = OrganizationSerializer.class)
    @JsonDeserialize(using = OrganizationDeserializer.class)
    protected OrganizationItem organization = null;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("englishName")
    protected String englishName;

    @JsonProperty("vegetarian")
    protected boolean vegetarian;

    @JsonProperty("price")
    protected float price;

    @JsonProperty("image")
    protected byte[] image;

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    public DailyMealItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationItem to be copied.
     */
    public DailyMealItem(DailyMealItem item) {
        super(item);
        this.organization = item.organization;
        this.name = item.name;
        this.englishName = item.englishName;
        this.vegetarian = item.vegetarian;
        this.price = item.price;
        this.image = item.image;
    }

    public DailyMealItem(int id, Date createDate, Date editDate, String createUser,
                         String editUser, Date date, OrganizationItem organization, String name,
                         String englishName, boolean vegetarian, float price, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date);
        this.organization = organization;
        this.name = name;
        this.englishName = englishName;
        this.vegetarian = vegetarian;
        this.price = price;
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

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(DailyMealItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.organization = updatedItem.organization;
        this.name = updatedItem.name;
        this.englishName = updatedItem.englishName;
        this.vegetarian = updatedItem.vegetarian;
        this.price = updatedItem.price;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "organization = [[" + this.organization.toString() + "]]" + "\n";
        result += "name = " + this.name + "\n";
        result += "englishName = " + this.englishName + "\n";
        result += "vegetarian = " + this.vegetarian + "\n";
        result += "price = " + this.price + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
