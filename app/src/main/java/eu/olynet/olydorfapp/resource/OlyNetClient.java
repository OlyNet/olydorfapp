/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
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

    /* News API */
    @GET
    @Path("/news/meta/{id}")
    public NewsMetaItem getMetaNews(@PathParam("id") int id);

    @GET
    @Path("/news/meta")
    public List<NewsMetaItem> getMetaNews();

    @GET
    @Path("/news/{id}")
    public NewsItem getNews(@PathParam("id") int id);

    @GET
    @Path("/news/any/{ids}")
    public List<NewsItem> getNews(@PathParam("ids") String ids);

    @GET
    @Path("/news")
    public List<NewsItem> getNews();

    /* MealOfTheDay API */
    @GET
    @Path("/mealoftheday/meta/{id}")
    public MealOfTheDayMetaItem getMetaMealoftheday(@PathParam("id") int id);

    @GET
    @Path("/mealoftheday/meta")
    public List<MealOfTheDayMetaItem> getMetaMealoftheday();

    @GET
    @Path("/mealoftheday/{id}")
    public MealOfTheDayItem getMealoftheday(@PathParam("id") int id);

    @GET
    @Path("/mealoftheday/any/{ids}")
    public List<MealOfTheDayItem> getMealoftheday(@PathParam("ids") String ids);

    @GET
    @Path("/mealoftheday")
    public List<MealOfTheDayItem> getMealoftheday();

    /* Food API */
    @GET
    @Path("/food/meta/{id}")
    public FoodMetaItem getMetaFood(@PathParam("id") int id);

    @GET
    @Path("/food/meta")
    public List<FoodMetaItem> getMetaFood();

    @GET
    @Path("/food/{id}")
    public FoodItem getFood(@PathParam("id") int id);

    @GET
    @Path("/food/any/{ids}")
    public List<FoodItem> getFood(@PathParam("ids") String ids);

    @GET
    @Path("/food")
    public List<FoodItem> getFood();

    /* DailyMeal API */
    @GET
    @Path("/dailymeal/meta/{id}")
    public DailyMealMetaItem getMetaDailymeal(@PathParam("id") int id);

    @GET
    @Path("/dailymeal/meta")
    public List<DailyMealMetaItem> getMetaDailymeal();

    @GET
    @Path("/dailymeal/{id}")
    public DailyMealItem getDailymeal(@PathParam("id") int id);

    @GET
    @Path("/dailymeal/any/{ids}")
    public List<DailyMealItem> getDailymeal(@PathParam("ids") String ids);

    @GET
    @Path("/dailymeal")
    public List<DailyMealItem> getDailymeal();

    /* Organization API */
    @GET
    @Path("/organization/meta/{id}")
    public OrganizationMetaItem getMetaOrganization(@PathParam("id") int id);

    @GET
    @Path("/organization/meta")
    public List<OrganizationMetaItem> getMetaOrganization();

    @GET
    @Path("/organization/{id}")
    public OrganizationItem getOrganization(@PathParam("id") int id);

    @GET
    @Path("/organization/any/{ids}")
    public List<OrganizationItem> getOrganization(@PathParam("ids") String ids);

    @GET
    @Path("/organization")
    public List<OrganizationItem> getOrganization();
}
