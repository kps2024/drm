package net.teleuptv.braintree.customer.resource;

import java.util.UUID;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.braintreegateway.Customer;
import com.braintreegateway.Plan;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.customer.dto.CreateCustomerDTO;
import net.teleuptv.braintree.customer.dto.NewCustomerPlanJourney;
import net.teleuptv.braintree.customer.service.CustomerService;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.plan.service.PlanService;
import net.teleuptv.braintree.subscription.service.SubscriptionService;
import net.teleuptv.common.AppResponse;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    PlanService planService;

    @Inject
    SubscriptionService subscriptionService;

    private static final Logger LOG = Logger.getLogger(CustomerResource.class);

    String trackingId = UUID.randomUUID().toString();

    //Create a customer
    @POST
    public Response createCustomer(@Valid CreateCustomerDTO dto){
        try{
            //Validation
            if(dto !=null){
                Result <Customer> result = customerService.CreateCustomer(dto);
                return (result!=null && result.isSuccess()) ? Response.ok().entity(result.getMessage()).build() : Response.status(Response.Status.BAD_REQUEST).entity(result.getMessage()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
            }
        } catch(Exception e){
            LOG.error("Failed creating customer. Exception occured: " +e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error").build();
        }
    }

    @POST
    @Path("/new-customer-plan-journey")
    public Response newCustomerPlanJourney(@NotNull @Valid NewCustomerPlanJourney dto){

        String trackingId = UUID.randomUUID().toString();
        MDC.put("trackingId", trackingId);

        LOG.infof("Starting subscription process for email: %s, planId: %s", dto.getEmail(), dto.getPlanId());

        // 1. Validate the plan
        LOG.infof("Validate the plan Id %s", dto.getPlanId());
        Plan plan =  planService.findPlan(dto.getPlanId());
        if(plan == null){
            LOG.warnf("Invalid plan id %s provided by customer email: %s", dto.getPlanId(), dto.getEmail());
            return AppResponse.badRequest("No such plan found");
        }

        try {
                // 2. Create customer
                LOG.info("Create Braintree customer with nonce from client");
                Result<Customer> customerResult = customerService.newCustomerPlanJourneyCreateCustomer(dto);
                                                                
                if(!customerResult.isSuccess()){
                    return AppResponse.internalServerError("Failed to create customer");
                }
                LOG.infof("Customer ID %s has been created at Braintree", customerResult.getTarget().getId());

                // 3. Get the payment token
                LOG.info("Get the payment Token");
                String paymentToken = customerResult.getTarget().getDefaultPaymentMethod().getToken();

                // 4. Create subscription
                LOG.info("Create Subscription by passing payment token and plan id");
                Result<Subscription> subscriptionResult = subscriptionService.newCustomerPlanJourneyCreateSubscription(paymentToken, dto.getPlanId());
                if(!subscriptionResult.isSuccess()){
                    LOG.error("Error ocurred while creating subscription in Braintree platform");
                    LOG.error("Braintree error message: " + subscriptionResult.getMessage());
                    return AppResponse.internalServerError("Failed to create subscription");
                }

                LOG.infof("Subscription ID %s has been created for the Customer %s with plan %s", 
                    subscriptionResult.getTarget().getId(),
                    customerResult.getTarget().getId(),
                    dto.getPlanId());

                LOG.info("Update the domain application customer details");
                customerService.createCustomerInDomainApp(dto, customerResult, subscriptionResult, trackingId);

                return AppResponse.success("Customer and Subscription created successfully");
                
        } catch(Exception e){
            LOG.error("Exception occurred while processing new customer plan journey" + e);
            return AppResponse.internalServerError("Failed to create customer and subscription");
        } finally{
            MDC.clear(); // important to avoid leaking tracking ID across threads
        }
    }
}
