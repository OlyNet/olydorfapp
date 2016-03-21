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

import eu.olynet.olydorfapp.resources.DailyMealDeserializer;
import eu.olynet.olydorfapp.resources.DailyMealSerializer;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties("mealOfTheDayRatings")
public class MealOfTheDayItem extends MealOfTheDayMetaItem {

    @JsonProperty("cook")
    protected String cook;

    @JsonProperty("price")
    protected float price;

    @JsonProperty("dailyMeal")
    @JsonSerialize(using = DailyMealSerializer.class)
    @JsonDeserialize(using = DailyMealDeserializer.class)
    protected DailyMealItem dailyMeal;

    @JsonProperty("image")
    protected byte[] image;

    /**
     * Default constructor for deserialization. <b>Do not use!</b>
     */
    protected MealOfTheDayItem() {
        super();
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the OrganizationItem to be copied.
     */
    public MealOfTheDayItem(MealOfTheDayItem item) {
        super(item);
        this.cook = item.cook;
        this.price = item.price;
        this.dailyMeal = item.dailyMeal;
        this.image = item.image;
    }

    public MealOfTheDayItem(int id, Date createDate, Date editDate, String createUser,
                            String editUser, Date date, Date lastUsedDate, String cook, float price,
                            DailyMealItem dailyMeal, byte[] image) {
        super(id, createDate, editDate, createUser, editUser, date, lastUsedDate);
        this.cook = cook;
        this.price = price;
        this.dailyMeal = dailyMeal;
        this.image = image;
    }

    public String getCook() {
        return cook;
    }

    public void setCook(String cook) {
        this.cook = cook;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public DailyMealItem getDailyMeal() {
        return dailyMeal;
    }

    public void setDailyMeal(DailyMealItem dailyMeal) {
        this.dailyMeal = dailyMeal;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(MealOfTheDayItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.cook = updatedItem.cook;
        this.price = updatedItem.price;
        this.dailyMeal = updatedItem.dailyMeal;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "cook = " + this.cook + "\n";
        result += "price = " + this.price + "\n";
        result += "dailyMeal = [[" + this.dailyMeal.toString() + "]]" + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
