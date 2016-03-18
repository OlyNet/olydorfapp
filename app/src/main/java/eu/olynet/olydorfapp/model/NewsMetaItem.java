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

import java.util.Comparator;
import java.util.Date;

import eu.olynet.olydorfapp.resources.OrganizationDeserializer;
import eu.olynet.olydorfapp.resources.OrganizationSerializer;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NewsMetaItem extends AbstractMetaItem<NewsMetaItem> {

    @JsonDeserialize(using = OrganizationDeserializer.class)
    @JsonSerialize(using = OrganizationSerializer.class)
    protected OrganizationItem organization = null;

    protected Date date;
    protected String title;
    protected String link;

    public NewsMetaItem(Date lastUsed) {
        super(lastUsed);
        this.organization = null;
        this.date = null;
        this.title = null;
        this.link = null;
    }

    public NewsMetaItem(int id) {
        super(id);
        this.organization = null;
        this.date = null;
        this.title = null;
        this.link = null;
    }

    /**
     * Copy constructor. Performs a shallow copy.
     *
     * @param item the NewsMetaItem to be copied.
     */
    public NewsMetaItem(NewsMetaItem item) {
        super(item);
        this.organization = item.organization;
        this.date = item.date;
        this.title = item.title;
        this.link = item.link;
    }

    @JsonCreator
    public NewsMetaItem(@JsonProperty("id") int id,
                        @JsonProperty("createDate") Date createDate,
                        @JsonProperty("editDate") Date editDate,
                        @JsonProperty("published") boolean published,
                        @JsonProperty("deleted") boolean deleted,
                        @JsonProperty("createUser") String createUser,
                        @JsonProperty("editUser") String editUser,
                        @JsonProperty("organization") OrganizationItem organization,
                        @JsonProperty("date") Date date,
                        @JsonProperty("title") String title,
                        @JsonProperty("link") String link) {
        super(id, createDate, editDate, published, deleted, createUser, editUser);
        this.organization = organization;
        this.date = date;
        this.title = title;
        this.link = link;
    }

    public OrganizationItem getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationItem organization) {
        this.organization = organization;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        String result = super.toString() + "\n";
        result += "organization = [[" + this.organization.toString() + "]]" + "\n";
        result += "date = " + this.date + "\n";
        result += "title = " + this.title + "\n";
        result += "link = " + this.link + "";

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof NewsMetaItem))
            return false;
        NewsMetaItem item = (NewsMetaItem) obj;

        return this.getId() == item.getId();
    }

    @Override
    public void updateItem(NewsMetaItem updatedItem) throws ItemMismatchException {
        super.updateItem(updatedItem);
        this.organization = updatedItem.organization;
        this.date = updatedItem.date;
        this.title = updatedItem.title;
        this.link = updatedItem.link;
    }

    /**
     * Comparator used to order items by their createDate in ascending order. A use case for this
     * would be displaying daily meals for the next month.
     */
    public static class DateAscComparator implements Comparator<NewsMetaItem> {
        @Override
        public int compare(NewsMetaItem lhs, NewsMetaItem rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    }

    /**
     * Comparator used to order items by their createDate in descending order. A use case for this
     * would be displaying news entries.
     */
    public static class DateDescComparator implements Comparator<NewsMetaItem> {
        @Override
        public int compare(NewsMetaItem lhs, NewsMetaItem rhs) {
            return -lhs.getDate().compareTo(rhs.getDate());
        }
    }
}
