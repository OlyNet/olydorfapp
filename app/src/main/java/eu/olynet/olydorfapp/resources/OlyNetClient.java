/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.olynet.olydorfapp.model.FoodItem;
import eu.olynet.olydorfapp.model.FoodMetaItem;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.model.OrganizationMetaItem;

/**
 * Interface for the connection to the server. Methods will be implemented by the ResteasyClient.
 * Every resource that is available via the ResourceManager needs to have a corresponding functions
 * (using the correct name) here.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 * @see eu.olynet.olydorfapp.resources.ResourceManager
 */
@Produces(MediaType.APPLICATION_JSON)
public interface OlyNetClient {

    /* News API */
    @GET
    @Path("/news/meta")
    public List<NewsMetaItem> getMetaNews();

    @GET
    @Path("/news/{id}")
    public NewsItem getNews(@PathParam("id") int id);

    @GET
    @Path("/news")
    public List<NewsItem> getNews();

    /* Food API */
    @GET
    @Path("/food/meta")
    public List<FoodMetaItem> getMetaFood();

    @GET
    @Path("/food/{id}")
    public FoodItem getFood(@PathParam("id") int id);

    @GET
    @Path("/food")
    public List<FoodItem> getFood();

    /* Organization API */
    @GET
    @Path("/organization/meta")
    public List<OrganizationMetaItem> getMetaOrganization();

    @GET
    @Path("/organization/{id}")
    public OrganizationItem getOrganization(@PathParam("id") int id);

    @GET
    @Path("/organization")
    public List<OrganizationItem> getOrganization();
}
