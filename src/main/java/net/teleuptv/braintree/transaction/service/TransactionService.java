package net.teleuptv.braintree.transaction.service;

import java.util.UUID;

import org.jboss.logging.Logger;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.transaction.dto.TransactionDTO;

@ApplicationScoped
public class TransactionService {

    private static final Logger LOG = Logger.getLogger(TransactionService.class);

    @Inject
    BraintreeProvider braintreeProvider;

    public Result<Transaction> Sale(TransactionDTO dto){

        //Existing customer 
        if(!dto.getCustomerId().isEmpty()){
            TransactionRequest request = new TransactionRequest()
                .amount(dto.getAmount())
                .paymentMethodNonce(dto.getPaymentMethodNonce())
                .customerId(dto.getCustomerId())
                .options()
                    .storeInVaultOnSuccess(true)
                    .done();
            
            Result<Transaction> result = braintreeProvider.gateway().transaction().sale(request);
            return result;
            
        } else {    
            //Transaction sale and create new customer
            //Generate customer id
            String newCustomerId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);

            TransactionRequest request = new TransactionRequest()
                .amount(dto.getAmount())
                .paymentMethodNonce(dto.getPaymentMethodNonce())
                .customer()
                    .id(newCustomerId)
                    .firstName(newCustomerId)
                    .lastName(newCustomerId)
                    .email(newCustomerId)
                    .phone(newCustomerId)
                    .done()
                .options()
                    .storeInVaultOnSuccess(true)
                    .done();
            
            Result<Transaction> result = braintreeProvider.gateway().transaction().sale(request);
            return result;
        }

        //

    }
}
