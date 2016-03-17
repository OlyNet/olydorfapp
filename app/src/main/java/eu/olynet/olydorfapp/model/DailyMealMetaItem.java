/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class DailyMealMetaItem extends AbstractMetaItem<DailyMealMetaItem> {

    protected int foodId;
    protected String cook;
    protected float price;

    public DailyMealMetaItem(Date lastUsed) {
        super(lastUsed);
        this.foodId = -1;
        this.cook = null;
        this.price = Float.NaN;
    }

    public DailyMealMetaItem(int id) {
        super(id);
        this.foodId = -1;
        this.cook = null;
        this.price = Float.NaN;
    }

    @JsonCreator
    public DailyMealMetaItem(@JsonProperty("id") int id,
                             @JsonProperty("createDate") Date createDate,
                             @JsonProperty("editDate") Date editDate,
                             @JsonProperty("published") boolean published,
                             @JsonProperty("deleted") boolean deleted,
                             @JsonProperty("createUser") String createUser,
                             @JsonProperty("editUser") String editUser,
                             @JsonProperty("foodId") int foodId,
                             @JsonProperty("cook")  String cook,
                             @JsonProperty("price") float price) {
        super(id, createDate, editDate, published, deleted, createUser, editUser);
        this.foodId = foodId;
        this.cook = cook;
        this.price = price;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof DailyMealMetaItem))
            return false;
        DailyMealMetaItem item = (DailyMealMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "foodId = " + this.foodId + "\n";
        result += "cook = " + this.cook + "\n";
        result += "price = " + this.price;

        return result;
    }

    @Override
    public void updateItem(DailyMealMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.foodId = updatedItem.foodId;
        this.cook = updatedItem.cook;
        this.price = updatedItem.price;
    }

    public static Comparator<DailyMealMetaItem> getDateDescComparator() {
        return new Comparator<DailyMealMetaItem>() {
            @Override
            public int compare(DailyMealMetaItem lhs, DailyMealMetaItem rhs) {
                return -lhs.getCreateDate().compareTo(rhs.getCreateDate());
            }
        };
    }
}
