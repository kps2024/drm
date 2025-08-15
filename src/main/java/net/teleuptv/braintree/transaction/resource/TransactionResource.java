package net.teleuptv.braintree.transaction.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.Customer;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.transaction.dto.ExistingCustomerTransactionDTO;
import net.teleuptv.braintree.transaction.dto.NewCustomerTransactionDTO;
import net.teleuptv.braintree.transaction.service.TransactionService;

@Path("/transaction")
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    private static final Logger LOG = Logger.getLogger(TransactionResource.class);

    @Inject
    TransactionService serviceTransaction;

    @Inject
    BraintreeProvider braintreeProvider;

    @POST
    @Path("/new-customer")
    public Response Sale(@Valid NewCustomerTransactionDTO dto){
        try{
            //Validate DTO
            if(dto!=null) {
                //Processing the Transaction sale
                Result<Transaction> result = serviceTransaction.NewCustomerTransactionSale(dto);
                return result.isSuccess() ? Response.ok().entity(result.getMessage()).build() : Response.status(Response.Status.BAD_REQUEST).entity(result.getMessage()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
            }
        
        } catch(Exception e){
            LOG.error("Transaction sale error: "+e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error").build();
        }
    }

    @POST
    @Path("/existing-customer")
    public Response Sale(@Valid ExistingCustomerTransactionDTO dto){
        try{
            //Validate DTO
            if(dto!=null && dto.getCustomerId().isEmpty()) {
                //Check if the customer ID exists
                Customer customer = braintreeProvider.gateway().customer().find(dto.getCustomerId());

                if(customer.getId() == dto.getCustomerId()){
                    // Customer found.
                    //Processing the Transaction sale
                    Result<Transaction> result = serviceTransaction.ExistingCustomerTransactionSale(dto);
                    return (result.isSuccess()) ? Response.ok().entity(result.getMessage()).build() : Response.status(Response.Status.BAD_REQUEST).entity(result.getMessage()).build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Customer not found.").build();
                }
            } else{
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request body").build();
            }
            
        } catch(Exception e){
            LOG.error("Transaction sale error: "+e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Server error").build();
        }
    }

}
