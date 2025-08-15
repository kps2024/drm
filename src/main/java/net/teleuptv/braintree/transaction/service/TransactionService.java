package net.teleuptv.braintree.transaction.service;

import java.util.UUID;

import org.jboss.logging.Logger;

import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.transaction.dto.ExistingCustomerTransactionDTO;
import net.teleuptv.braintree.transaction.dto.NewCustomerTransactionDTO;

@ApplicationScoped
public class TransactionService {

    private static final Logger LOG = Logger.getLogger(TransactionService.class);

    @Inject
    BraintreeProvider braintreeProvider;

    public Result<Transaction> ExistingCustomerTransactionSale(ExistingCustomerTransactionDTO dto){

        //Existing customer 
        LOG.info("Creating transaction for an existing customer");
        LOG.debug("DTO received: " + dto);
        TransactionRequest request = new TransactionRequest()
            .amount(dto.getAmount())
            .paymentMethodNonce(dto.getPaymentMethodNonceFromClient())
            .customerId(dto.getCustomerId())
            .options()
                .storeInVaultOnSuccess(true)
                .done();
        
        Result<Transaction> result = braintreeProvider.gateway().transaction().sale(request);
        LOG.debug("Transaction result: " + result);
        LOG.debug("Transaction process completed.");
        return result; 
    }

    public Result<Transaction> NewCustomerTransactionSale(NewCustomerTransactionDTO dto){
            //Transaction sale and create new customer
            //Generate customer id
            String newCustomerId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
                
            LOG.info("Creating transaction for a new customer");
            LOG.debug("DTO received: " + dto);

            TransactionRequest request = new TransactionRequest()
                .amount(dto.getAmount())
                .paymentMethodNonce(dto.getPaymentMethodNonceFromClient())
                .customer()
                    .id(newCustomerId)
                    .firstName(dto.getCustomer().getFirstName())
                    .lastName(dto.getCustomer().getLastname())
                    .email(dto.getCustomer().getEmail())
                    .phone(dto.getCustomer().getPhone())            // We skip the credit card validation as it is out of the scope now. 
                    .done()
                .options()
                    .storeInVaultOnSuccess(true)
                    .done();
            
            Result<Transaction> result = braintreeProvider.gateway().transaction().sale(request);
            LOG.debug("Transaction result: " + result);
            LOG.info("Transaction process completed.");
            return result;
    }
}
