package net.teleuptv.braintree.clienttoken.service;

import org.jboss.logging.Logger;

import com.braintreegateway.ClientTokenRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.clienttoken.dto.ClientTokenDTO;
import net.teleuptv.braintree.gateway.BraintreeProvider;

@ApplicationScoped
public class ClientTokenService {

    private static final Logger LOG = Logger.getLogger(ClientTokenService.class);

    @Inject
    BraintreeProvider braintreeProvider;

    public String generateClientToken(ClientTokenDTO dto, Boolean hasCustomerId){

        ClientTokenRequest clientTokenRequest;

        if(hasCustomerId){
            clientTokenRequest = new ClientTokenRequest()
                .customerId(dto.getCustomerID())
                .merchantAccountId(dto.getMerchantAccountId());
        } else {
            clientTokenRequest = new ClientTokenRequest()
                .merchantAccountId(dto.getMerchantAccountId());
        }

        if(clientTokenRequest != null){
            return braintreeProvider.gateway().clientToken().generate(clientTokenRequest);
        } else {
            LOG.info("Customer ID or Merchant Account ID is invalid! ");
            return null;

        }
        
        
    }
}
