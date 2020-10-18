package moe.yo3explorer.mfcProxy.boundary;

import moe.yo3explorer.mfcProxy.control.ItemParser;
import moe.yo3explorer.mfcProxy.model.AdModel;
import moe.yo3explorer.mfcProxy.model.ItemModel;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/item")
public class Item {

    @Inject
    ItemParser itemParser;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getItem(@PathParam("id") int id)
    {
        String url = String.format("https://myfigurecollection.net/item/%d/",id);
        ItemModel parse = itemParser.parse(url);
        return Response.ok(parse).build();
    }
}
