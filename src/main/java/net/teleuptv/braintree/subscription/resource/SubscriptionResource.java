package net.teleuptv.braintree.subscription.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.Plan;
import com.braintreegateway.ResourceCollection;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionSearchRequest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.plan.service.PlanService;
import net.teleuptv.braintree.subscription.dto.CreateSubscriptionDTO;
import net.teleuptv.braintree.subscription.service.SubscriptionService;


@Path("/subscription")
@Consumes(MediaType.APPLICATION_JSON)
public class SubscriptionResource {

    private static final Logger LOG = Logger.getLogger(SubscriptionResource.class);

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    SubscriptionService subscriptionService;

    @Inject
    PlanService planService;

    @GET
    @Path("/status")
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
    public Response CreateSubscription(@Valid CreateSubscriptionDTO dto){
        LOG.info("Create Subscription");
        try{
            //Validate dto
            LOG.info("Validating the Create subscription DTO: " + dto);
            if (dto.getPaymentMethodNonceFromClient() == null && dto.getPlanId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Token and PlanId are required.")
                    .build();
            }

            LOG.debug("Validating plan: " + dto.getPlanId() );
            Plan plan = planService.findPlan(dto.getPlanId());
            if(plan == null){
                LOG.error("No such plan available.");
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No such plan available.")
                    .build();
            }

            LOG.info("Creating a new subscription");
            Result<Subscription> result = subscriptionService.createSubscription(dto);
            return result.isSuccess() 
                ? Response.ok().entity(result.getMessage()).build() 
                : Response.status(Response.Status.BAD_REQUEST).entity(result.getMessage()).build();
        
        } catch(Exception e){
            LOG.error("Create subscription error: " +e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error").build();
        }        
    }
}
