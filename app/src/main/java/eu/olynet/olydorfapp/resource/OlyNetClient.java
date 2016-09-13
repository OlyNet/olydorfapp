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
package eu.olynet.olydorfapp.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.olynet.olydorfapp.model.DailyMealItem;
import eu.olynet.olydorfapp.model.DailyMealMetaItem;
import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.MealOfTheDayItem;
import eu.olynet.olydorfapp.model.MealOfTheDayMetaItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * Interface for the connection to the server. Methods will be implemented by the ResteasyClient.
 * Every resource that is available via the ResourceManager needs to have a corresponding functions
 * (using the correct name) here.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 * @see ProductionResourceManager
 */
@Produces(MediaType.APPLICATION_JSON)
public interface OlyNetClient {

    @GET
    @Path("{type}/{id}/{field}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    byte[] getImage(@PathParam("type") String type, @PathParam("id") int id,
                    @PathParam("field") String field);

    /* News API */
    @GET
    @Path("/news/meta/{id}")
    NewsMetaItem getMetaNews(@PathParam("id") int id);

    @GET
    @Path("/news/meta")
    List<NewsMetaItem> getMetaNews();

    @GET
    @Path("/news/{id}")
    NewsItem getNews(@PathParam("id") int id);

    @GET
    @Path("/news/any/{ids}")
    List<NewsItem> getNews(@PathParam("ids") String ids);

    @GET
    @Path("/news")
    List<NewsItem> getNews();

    /* MealOfTheDay API */
    @GET
    @Path("/mealoftheday/meta/{id}")
    MealOfTheDayMetaItem getMetaMealoftheday(@PathParam("id") int id);

    @GET
    @Path("/mealoftheday/meta")
    List<MealOfTheDayMetaItem> getMetaMealoftheday();

    @GET
    @Path("/mealoftheday/{id}")
    MealOfTheDayItem getMealoftheday(@PathParam("id") int id);

    @GET
    @Path("/mealoftheday/any/{ids}")
    List<MealOfTheDayItem> getMealoftheday(@PathParam("ids") String ids);

    @GET
    @Path("/mealoftheday")
    List<MealOfTheDayItem> getMealoftheday();

    /* Food API */
    @GET
    @Path("/food/meta/{id}")
    FoodMetaItem getMetaFood(@PathParam("id") int id);

    @GET
    @Path("/food/meta")
    List<FoodMetaItem> getMetaFood();

    @GET
    @Path("/food/{id}")
    FoodItem getFood(@PathParam("id") int id);

    @GET
    @Path("/food/any/{ids}")
    List<FoodItem> getFood(@PathParam("ids") String ids);

    @GET
    @Path("/food")
    List<FoodItem> getFood();

    /* DailyMeal API */
    @GET
    @Path("/dailymeal/meta/{id}")
    DailyMealMetaItem getMetaDailymeal(@PathParam("id") int id);

    @GET
    @Path("/dailymeal/meta")
    List<DailyMealMetaItem> getMetaDailymeal();

    @GET
    @Path("/dailymeal/{id}")
    DailyMealItem getDailymeal(@PathParam("id") int id);

    @GET
    @Path("/dailymeal/any/{ids}")
    List<DailyMealItem> getDailymeal(@PathParam("ids") String ids);

    @GET
    @Path("/dailymeal")
    List<DailyMealItem> getDailymeal();

    /* Organization API */
    @GET
    @Path("/organization/meta/{id}")
    OrganizationMetaItem getMetaOrganization(@PathParam("id") int id);

    @GET
    @Path("/organization/meta")
    List<OrganizationMetaItem> getMetaOrganization();

    @GET
    @Path("/organization/{id}")
    OrganizationItem getOrganization(@PathParam("id") int id);

    @GET
    @Path("/organization/any/{ids}")
    List<OrganizationItem> getOrganization(@PathParam("ids") String ids);

    @GET
    @Path("/organization")
    List<OrganizationItem> getOrganization();
}
