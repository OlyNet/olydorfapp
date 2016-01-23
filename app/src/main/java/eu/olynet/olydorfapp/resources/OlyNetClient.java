package eu.olynet.olydorfapp.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.NewsMetaItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
@Produces(MediaType.APPLICATION_JSON)
public interface OlyNetClient {

    @GET
    @Path("/news/meta")
    @Produces("application/json")
    public List<NewsMetaItem> getMetaNews();

    @GET
    @Path("/news/{id}")
    public NewsItem getNews(@PathParam("id") int id);
}
