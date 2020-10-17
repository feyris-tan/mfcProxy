package moe.yo3explorer.mfcProxy;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

@Singleton
@Path("/fetch")
@RegisterRestClient(configKey = "kotori-client")
public interface KotoriClient {
    @GET
    byte[] performFetch(@HeaderParam("Kotori-URL") String url);
}
