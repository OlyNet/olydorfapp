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
public class FoodItem extends FoodMetaItem {

    protected byte[] image;

    public FoodItem() {
        super();
    }

    public FoodItem(int id, Date date, Date createDate, Date editDate, boolean published,
                    boolean deleted, String createUser, String editUser, Organization organization,
                    String name, String englishname, float price, boolean vegetarian, byte[] image) {
        super(id, date, createDate, editDate, published, deleted, createUser, editUser,
                organization, name, englishname, price, vegetarian);
        this.image = image;
    }

    public byte[] getImage() {
        this.setLastUsedDate();
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void updateItem(FoodItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
