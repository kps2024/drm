package net.teleuptv.braintree.customer.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.Customer;
import com.braintreegateway.Result;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.customer.dto.CreateCustomerDTO;
import net.teleuptv.braintree.customer.service.CustomerService;
import net.teleuptv.braintree.gateway.BraintreeProvider;

@Path("/customer")
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @Inject
    BraintreeProvider braintreeProvider;

    private static final Logger LOG = Logger.getLogger(CustomerResource.class);

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
}
