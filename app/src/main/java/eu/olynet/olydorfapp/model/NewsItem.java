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
public class NewsItem extends NewsMetaItem {

    protected String text;
    protected byte[] image;

    public NewsItem() {
        super();
    }

    public NewsItem(int id, Date date, Date createDate, Date editDate, boolean published,
                        boolean deleted, String createUser, String editUser,
                        Organization organization, String title, String text, byte[] image) {
        super(id, date, createDate, editDate, published, deleted, createUser, editUser,
                organization, title);
        this.text = text;
        this.image = image;
    }

    public String getText() {
        this.setLastUsedDate();
        return text;
    }

    public void setText(String content) {
        this.text = content;
    }

    public byte[] getImage() {
        this.setLastUsedDate();
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    
    public void updateItem(NewsItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.text = updatedItem.text;
        this.image = updatedItem.image;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "text = " + this.text + "\n";
        result += "image = " + ((image != null) ? image.length : 0) + " Byte";

        return result;
    }
}
