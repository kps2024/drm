package net.teleuptv.drmauth.test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import io.quarkus.logging.Log;
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
    public Response testPlayreadyAuth(@Context UriInfo uriInfo, @QueryParam("email") String emailParam, @QueryParam("CustomData") String CustomData) {

        try{
            String playbackID = java.util.UUID.randomUUID().toString();
            boolean authorized = false;
            String pX =null;
            String email = null;

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

            //Only Email 

            if (emailParam == null || emailParam.isBlank()) {
                LOG.info("Email is required! " + playbackID);
                authorized = false;
            } else {

                Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

                String emaill = Arrays.stream(emailParam.split("[|,]"))
                .filter(s -> s.contains("@"))
                .findFirst()
                .orElse("N/A");

                if (EMAIL_PATTERN.matcher(emaill).matches()) {
                    LOG.info("Email ok: " + emaill);
                    authorized = true;
                } else {
                    LOG.warn("Invalid Email: " + emaill);
                    authorized = false;
                }
            }

            //Both email and pX 
            if (CustomData == null || CustomData.isBlank()) {
                LOG.info("CustomData(pX) is required! " + playbackID);
                authorized = false;
            } else {
                //pX
                // Regex pattern to match pX values
                Pattern pXPattern = Pattern.compile("pX=([A-Za-z0-9]+)");
                Matcher pXMatcher = pXPattern.matcher(CustomData);

                //Email
                // Regex pattern to match email values
                Pattern emailPattern = Pattern.compile("email=([\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,})");
                Matcher emailMatcher = emailPattern.matcher(CustomData);

                if (emailMatcher.find() && pXMatcher.find()) {
                    email = emailMatcher.group(1);
                    pX = pXMatcher.group(1);
                    Log.info("First pX: " + pX + ", First email: "+email);
                    authorized = true;
                } else {
                    authorized = false;
                }
            }

            if (authorized) {
                LOG.info("Playback request accepted for " + playbackID);
                return Response.ok()
                            .entity("pX="+pX)
                            .build();             // accept response is 200 OK and the return value is pX=A359F2
            } else {
                LOG.info("Playback request denied for " + playbackID );
                return Response.ok().build();       // deny response is 200 OK only, not return value
            }
        } catch (Exception e){
            Log.error("Playback request denied and error occured : " + e);
            //return Response.status(Response.Status.BAD_REQUEST).build();
            return Response.ok().build();   // deny response is 200 OK only, not return value
        }        
    }
}
