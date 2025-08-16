package net.teleuptv.common;

import java.util.UUID;

import jakarta.ws.rs.core.Response;
import lombok.Data;

@Data
public class AppResponse {

    //DTO for error responses
    private Response.Status status;
    private String error;
    private String message;
    private String trackingId;

    public AppResponse(Response.Status status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.trackingId = UUID.randomUUID().toString();
    }

    public static Response internalServerError(String message){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new AppResponse(
                        Response.Status.INTERNAL_SERVER_ERROR, 
                        "Internal Server Error", 
                        message))
                    .build();
    }

    public static Response badRequest(String message){
        return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new AppResponse(
                            Response.Status.BAD_REQUEST, 
                            "Wrong input",
                            message))
                        .build();
    }

    public static Response success(String message){
        return Response.ok()
                        .entity(new AppResponse(
                            Response.Status.ACCEPTED, 
                            "No error", 
                            message))
                        .build();
    }

}
