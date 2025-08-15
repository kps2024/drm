package net.teleuptv.braintree.subscription.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.ResourceCollection;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionSearchRequest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.subscription.dto.CreateSubscriptionRequestDTO;
import net.teleuptv.braintree.subscription.service.SubscriptionService;


@Path("/subscription")
public class SubscriptionResource {

    private static final Logger LOG = Logger.getLogger(SubscriptionResource.class);

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    SubscriptionService subscriptionService;

    @GET
    @Path("/search/status")
    public Response SubscriptionStatus(){
        LOG.info("Subscription searching by status");
        try {
            SubscriptionSearchRequest request = new SubscriptionSearchRequest()
                .status().in(
                    Subscription.Status.ACTIVE,
                    Subscription.Status.CANCELED,
                    Subscription.Status.EXPIRED,
                    Subscription.Status.PAST_DUE,
                    Subscription.Status.PENDING
                );
            
            ResourceCollection<Subscription> collection = braintreeProvider.gateway().subscription().search(request);

            for(Subscription subscription: collection){
                LOG.info(subscription.getStatus());
            }   
        } catch(Exception e){
            LOG.error("Subscription status exception: " + e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/create")
    public Response CreateSubscription(@Valid CreateSubscriptionRequestDTO dto){
        LOG.info("Create Subscription");
        try{
            //Validate dto
            if (dto.getPaymentToken() == null || dto.getPlanId() == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Token and PlanId are required.").build();
            }

            //Process the subscription
            Result<Subscription> result = subscriptionService.create(dto);
            return Response.ok().entity(result).build(); 
        } catch(Exception e){
            LOG.error("Create subscription error: "+e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }        
    }
}
