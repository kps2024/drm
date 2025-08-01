package net.teleuptv.drmauth.test;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/drmauth/test/playready")
@Produces(MediaType.APPLICATION_JSON)
public class PlayreadyAuth {

    private static final Logger LOG = Logger.getLogger(PlayreadyAuth.class);


    @GET
    public Response testPlayreadyAuth(@Context UriInfo uriInfo, @QueryParam("email") String emailParam) {
        String playbackID = java.util.UUID.randomUUID().toString();
        boolean authorized = false;
        

        LOG.info("ping response for playready");

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

        
        if (emailParam == null || emailParam.isBlank()) {
            LOG.info("Email is required! " + playbackID);
            authorized = false;
        } else {

            Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

            String email = Arrays.stream(emailParam.split("[|,]"))
            .filter(s -> s.contains("@"))
            .findFirst()
            .orElse("N/A");

            if (EMAIL_PATTERN.matcher(email).matches()) {
                LOG.info("Email ok: " + email);
                authorized = true;
            } else {
                LOG.warn("Invalid Email: " + email);
                authorized = false;
            }
        }

        if (authorized) {
            LOG.info("Request allowed " + playbackID);
            return Response.ok()
                           .entity("{\"result\":\"allowed\"}")
                           .build();
        } else {
            LOG.info("Request denied " + playbackID );
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"result\":\"denied\"}")
                           .build();
        }

        
    }
}
