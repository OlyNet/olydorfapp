/*
 * Copyright (c) OlyNet 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */

package eu.olynet.olydorfapp.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "abstractType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrganizationMetaItem.class, name = "OrganizationMetaItem"),
        @JsonSubTypes.Type(value = OrganizationItem.class, name = "OrganizationItem"),
        @JsonSubTypes.Type(value = NewsMetaItem.class, name = "NewsMetaItem"),
        @JsonSubTypes.Type(value = NewsItem.class, name = "NewsItem"),
        @JsonSubTypes.Type(value = MealOfTheDayMetaItem.class, name = "MealOfTheDayMetaItem"),
        @JsonSubTypes.Type(value = MealOfTheDayItem.class, name = "MealOfTheDayItem"),
        @JsonSubTypes.Type(value = DailyMealMetaItem.class, name = "DailyMealMetaItem"),
        @JsonSubTypes.Type(value = DailyMealItem.class, name = "DailyMealItem")
})
abstract public class AbstractMetaItemMixIn {
    /* leave empty */
}
