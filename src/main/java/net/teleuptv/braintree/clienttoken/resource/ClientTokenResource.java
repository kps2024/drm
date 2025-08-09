package net.teleuptv.braintree.clienttoken.resource;

import org.jboss.logging.Logger;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.clienttoken.dto.ClientTokenDTO;
import net.teleuptv.braintree.clienttoken.service.ClientTokenService;
import net.teleuptv.braintree.gateway.BraintreeProvider;

@Path("/clienttoken")
public class ClientTokenResource {

    private static final Logger LOG = Logger.getLogger(ClientTokenResource.class);

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    ClientTokenService clientTokenService;

    @POST
    @Path("/generate")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    //Generate Client Token
    public Response generateClientToken(ClientTokenDTO dto){
        try {
            LOG.info("Generate Client Token");
            Boolean hasCustomerId = false;
            //Validate DTO
            if(dto.getMerchantAccountId() != null){
                LOG.info("Merchant Account ID: "+ dto.getMerchantAccountId());
                if(dto.getCustomerID() != null ){
                    hasCustomerId = true;
                    LOG.info("Customer ID: "+ dto.getCustomerID());
                }
            }

            // Process the generate Client Token
            String clientToken = clientTokenService.generateClientToken(dto, hasCustomerId);
          
            return (clientToken != null) ? Response.ok().entity(clientToken).build() : Response.status(Response.Status.BAD_REQUEST).entity(null).build();

        } catch(Exception e){
            LOG.error("Generate client token error: "+e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
