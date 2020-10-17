package moe.yo3explorer.mfcProxy.boundary;

import moe.yo3explorer.mfcProxy.control.PartnerRedirectParser;
import moe.yo3explorer.mfcProxy.model.PartnerRedirectModel;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/partner")
public class PartnerRedirect
{
    @Inject
    PartnerRedirectParser partnerRedirectParser;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartner(@PathParam("id") int id)
    {
        String url = String.format("https://myfigurecollection.net/partner/%d/",id);
        PartnerRedirectModel parse = partnerRedirectParser.parse(url);
        return Response.ok(parse).build();
    }
}
