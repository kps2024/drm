package net.teleuptv.braintree.transaction.resource;

import org.jboss.logging.Logger;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.transaction.dto.TransactionDTO;
import net.teleuptv.braintree.transaction.service.TransactionService;

@Path("/transaction")
public class TransactionResource {

    private static final Logger LOG = Logger.getLogger(TransactionResource.class);

    @Inject
    TransactionService serviceTransaction;

    @POST
    @Path("/new-customer")
    public Response Sale(NewCustomerTransactionDTO dto){
        try{
            //Validate DTO
            if(dto!=null) {
                
                //Processing the Transaction sale
                Result<Transaction> result = serviceTransaction.NewCustomerTransactionSale(dto);
                return (result.isSuccess()) ? Response.ok().entity(result).build() : Response.status(Response.Status.BAD_REQUEST).entity(result).build();
            }
            
        } catch(Exception e){
            LOG.error("Transaction sale error: "+e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
		return null;
    }

    @POST
    @Path("existing-customer")
    public Response Sale(ExistingCustomerTransactionDTO dto){
        try{
            //Validate DTO
            if(dto!=null) {
                //Processing the Transaction sale
                Result<Transaction> result = serviceTransaction.ExistingCustomerTransactionSale(dto);
                return (result.isSuccess()) ? Response.ok().entity(result).build() : Response.status(Response.Status.BAD_REQUEST).entity(result).build();
            }
            
        } catch(Exception e){
            LOG.error("Transaction sale error: "+e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
