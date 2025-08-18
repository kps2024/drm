package net.teleuptv.braintree.clienttoken.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.clienttoken.dto.ClientTokenDTO;
import net.teleuptv.braintree.clienttoken.dto.PaymentRequestDTO;
import net.teleuptv.braintree.clienttoken.service.ClientTokenService;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.subscription.service.SubscriptionService;

@Path("/client")
public class ClientTokenResource {

    private static final Logger LOG = Logger.getLogger(ClientTokenResource.class);

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    ClientTokenService clientTokenService;

    @Inject
    SubscriptionService subscriptionService;

    @GET
    @Path("/token")
    @Produces(MediaType.TEXT_PLAIN)
    //Generate Client Token
    // public Response generateClientToken(@Valid ClientTokenDTO dto){
    public String generateClientToken(){
        try {
            LOG.info("Generate Client Token");
            Boolean hasCustomerId = false;
            //Validate DTO
            // if(dto.getCustomerID() != null ){
            //     hasCustomerId = true;
            //     LOG.info("Customer ID: "+ dto.getCustomerID());
            // }

            // Process the generate Client Token
            //String clientToken = clientTokenService.generateClientToken(dto, hasCustomerId);
            String clientToken = clientTokenService.generateClientToken();
          
            //return (clientToken != null) ? Response.ok().entity(clientToken).build() : Response.status(Response.Status.BAD_REQUEST).entity(null).build();
            return (clientToken != null) ? clientToken : null;

        } catch(Exception e){
            LOG.error("Generate client token error: "+e);
            //return Response.status(Response.Status.BAD_REQUEST).build();
            return null;
        }
    }


    // // Receive payment_method_nonce from client application.
    // @POST
    // @Path("/checkout")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response checkout(@QueryParam("payment_method_nonce") String paymentMethodNonce){
        
    //     if (paymentMethodNonce == null || paymentMethodNonce.isEmpty()) {
    //         return Response.status(Response.Status.BAD_REQUEST)
    //                        .entity("Missing payment_method_nonce")
    //                        .build();
    //     }
    //     return Response.ok("Nonce received: " + paymentMethodNonce).build();
    // }

    @POST
    @Path("/checkout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout(
        @QueryParam("payment_method_nonce") String paymentMethodNonce,
        @QueryParam("plan_id") String planId) {
    
        if (paymentMethodNonce == null || paymentMethodNonce.isEmpty()) {
            LOG.error("Paymetodnonce failed");
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\": false, \"message\": \"Missing payment_method_nonce\"}")
                        .build();
        }
        
        if (planId == null || planId.isEmpty()) {
            LOG.error("Planid failed");
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"success\": false, \"message\": \"Missing plan_id\"}")
                        .build();
        }
        
        try {
            // Braintree subscription creation logic
            SubscriptionRequest request = new SubscriptionRequest()
                .planId(planId)
                .paymentMethodNonce(paymentMethodNonce);
            
            Result<Subscription> result = braintreeProvider.gateway().subscription().create(request);
            
            if (result.isSuccess()) {
                Subscription subscription = result.getTarget();
                return Response.ok("{\"success\": true, \"message\": \"Subscription created successfully\", \"subscription_id\": \"" + subscription.getId() + "\"}")
                               .build();
            } else {
                LOG.error("subscription failed" + result.getMessage() + " result: " + result);
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity("{\"success\": false, \"message\": \"" + result.getMessage() + "\"}")
                               .build();
            }
            
            // For now, return success (replace with actual Braintree code above)
            // return Response.ok("{\"success\": true, \"message\": \"Subscription created successfully\", \"subscription_id\": \"sub_123\"}")
            //             .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"success\": false, \"message\": \"Internal server error\"}")
                        .build();
        }
    }
}
