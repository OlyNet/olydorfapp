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

    public FoodItem(int id, Date date, Date lastUpdated, String name, float price,
                        boolean vegetarian, int organization, byte[] image) {
        super(id, date, lastUpdated, name, price, vegetarian, organization);
        this.image = image;
    }

    public byte[] getImage() {
        this.setLastUsed();
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
        result += "image = " + image.length + " Byte";

        return result;
    }
}
