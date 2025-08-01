package net.teleuptv.drmauth.test;



import java.util.Collections;
import java.util.Map;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/drmauth/test/playready")
public class PlayreadyAuth {

    private static final Logger LOG = Logger.getLogger(PlayreadyAuth.class);

    @Inject
    HttpServerRequest request;

    @GET
    public Response testPlayreadyAuth(
        @QueryParam("IP") String ip,
        @QueryParam("Token") String token,
        @QueryParam("PX") String pX,
        @QueryParam("Custom Data") String customData) {

        LOG.info("ping response for playready");

        // if (customData == null || customData.isBlank()) {
        //     //return Collections.emptyMap();
        //     return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
        // }

        LOG.info("EZDRM auth callback:");
        LOG.info("IP :" + ip);
        LOG.info("Token :" + token);
        LOG.info("PX :" + pX);
        LOG.info("Custom Data :" + customData);
        
        LOG.errorf("HTTP Request to %s failed, error id: %s",
                request.uri(),
                java.util.UUID.randomUUID()
        );
        //return Response.ok("License granted").build();
        return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
    }
}
