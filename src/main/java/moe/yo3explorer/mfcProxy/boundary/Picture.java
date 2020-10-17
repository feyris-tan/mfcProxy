package moe.yo3explorer.mfcProxy.boundary;

import moe.yo3explorer.mfcProxy.control.PictureParser;
import moe.yo3explorer.mfcProxy.model.PartnerRedirectModel;
import moe.yo3explorer.mfcProxy.model.PictureModel;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/picture")
public class Picture {
    @Inject
    PictureParser pictureParser;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPicture(@PathParam("id") int id)
    {
        String url = String.format("https://myfigurecollection.net/picture/%d/",id);
        PictureModel parse = pictureParser.parse(url);
        return Response.ok(parse).build();
    }
}
