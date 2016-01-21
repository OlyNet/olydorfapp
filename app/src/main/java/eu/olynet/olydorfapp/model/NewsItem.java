package eu.olynet.olydorfapp.model;

import java.util.Date;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NewsItem extends NewsMetaItem {

    private String content;

    public NewsItem() {

    }

    public NewsItem(long id, Date date, Date lastUpdated, String title, String author,
                    Organization organization, String content) {
        super(id, date, lastUpdated, title, author, organization);
        this.content = content;
    }

    public String getContent() {
        this.setLastUsed();
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public void updateItem(NewsItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.content = updatedItem.content;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "content = " + this.content;

        return result;
    }
}
