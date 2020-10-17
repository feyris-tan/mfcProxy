package moe.yo3explorer.mfcProxy.boundary;

import moe.yo3explorer.mfcProxy.control.AdParser;
import moe.yo3explorer.mfcProxy.model.AdModel;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/classified")
public class Ad {

    @Inject
    AdParser adParser;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAd(@PathParam("id") int id)
    {
        String url = String.format("https://myfigurecollection.net/classified/%d/",id);
        AdModel parse = adParser.parse(url);
        return Response.ok(parse).build();
    }
}
