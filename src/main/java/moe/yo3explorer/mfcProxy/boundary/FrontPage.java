package moe.yo3explorer.mfcProxy.boundary;

import moe.yo3explorer.mfcProxy.SimpleWebClient;
import moe.yo3explorer.mfcProxy.control.FrontPageParser;
import moe.yo3explorer.mfcProxy.control.NullParser;
import moe.yo3explorer.mfcProxy.model.FrontPageModel;
import org.jsoup.Jsoup;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Path("/")
public class FrontPage {

    @Inject
    FrontPageParser frontPageParser;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFrontPage()
    {
        String url = "https://myfigurecollection.net/";
        FrontPageModel parse = frontPageParser.parse(url);
        return Response.ok(parse).build();
    }
}
