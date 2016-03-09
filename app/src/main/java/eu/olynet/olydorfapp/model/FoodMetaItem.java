/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class FoodMetaItem extends AbstractMetaItem<FoodMetaItem> {

    protected String name;
    protected String englishname;
    protected float price;
    protected boolean vegetarian;

    public FoodMetaItem() {
        super();
    }

    public FoodMetaItem(Date lastUsed) {
        super(lastUsed);
        this.name = null;
        this.englishname = null;
        this.price = Float.NaN;
        this.vegetarian = false;
    }

    public FoodMetaItem(int id) {
        super(id);
        this.name = null;
        this.englishname = null;
        this.price = Float.NaN;
        this.vegetarian = false;
    }

    public FoodMetaItem(int id, Date date, Date createDate, Date editDate, boolean published,
                        boolean deleted, String createUser, String editUser,
                        Organization organization, String name, String englishname, float price,
                        boolean vegetarian) {
        super(id, date, createDate, editDate, published, deleted, createUser, editUser,
                organization);
        this.name = name;
        this.englishname = englishname;
        this.price = price;
        this.vegetarian = vegetarian;
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
        result += "name = " + this.name + "\n";
        result += "englishname = " + this.englishname + "\n";
        result += "price = " + this.price + "\n";
        result += "vegetarian = " + this.vegetarian;

        return result;
    }

    @Override
    public void updateItem(FoodMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.name = updatedItem.name;
        this.englishname = updatedItem.englishname;
        this.price = updatedItem.price;
        this.vegetarian = updatedItem.vegetarian;
    }
}
