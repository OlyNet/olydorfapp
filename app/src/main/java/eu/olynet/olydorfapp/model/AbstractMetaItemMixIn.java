/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
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
@JsonSubTypes(
        {@JsonSubTypes.Type(value = OrganizationMetaItem.class, name = "OrganizationMetaItem"),
         @JsonSubTypes.Type(value = OrganizationItem.class, name = "OrganizationItem"),
         @JsonSubTypes.Type(value = NewsMetaItem.class, name = "NewsMetaItem"),
         @JsonSubTypes.Type(value = NewsItem.class, name = "NewsItem"),
         @JsonSubTypes.Type(value = MealOfTheDayMetaItem.class, name = "MealOfTheDayMetaItem"),
         @JsonSubTypes.Type(value = MealOfTheDayItem.class, name = "MealOfTheDayItem"),
         @JsonSubTypes.Type(value = DailyMealMetaItem.class, name = "DailyMealMetaItem"),
         @JsonSubTypes.Type(value = DailyMealItem.class, name = "DailyMealItem")})
abstract public class AbstractMetaItemMixIn {
    /* leave empty */
}
