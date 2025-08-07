package net.teleuptv.drmauth.test;

import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/drmauth/test/fairplay")
@Produces(MediaType.TEXT_PLAIN)
public class FairplayAuth {
    private static final Logger LOG = Logger.getLogger(FairplayAuth.class);

    @GET
    public Response testFairplayAuth(@Context UriInfo uriInfo){
        LOG.info("ping response for fairplay");
        try {
                String playbackID = java.util.UUID.randomUUID().toString();
                boolean authorized = false;

                LOG.infof(
                    "\n--- Incoming Request ---\n" +
                    "Path       : %s\n" +
                    "Query Params:\n%s\n" +
                    "Playback ID   : %s\n" +
                    "------------------------",
                    uriInfo.getPath(),
                    uriInfo.getQueryParameters()
                        .entrySet()
                        .stream()
                        .map(e -> String.format("  %s = %s", e.getKey(), String.join(",", e.getValue())))
                        .reduce("", (a, b) -> a + b + "\n"),
                    playbackID
                );

                

                if (authorized) {
                    LOG.info("Playback request accepted for " + playbackID);
                    return Response.ok()
                                .entity("play=true")
                                .build();             
                } else {
                    LOG.info("Playback request denied for " + playbackID );
                    return Response.ok().entity("play=false").build();       
                }

        } catch(Exception e){
            LOG.error("Playback request denied and error occured : " + e);
            return Response.ok().entity("play=false").build();  
        }
    }
}
