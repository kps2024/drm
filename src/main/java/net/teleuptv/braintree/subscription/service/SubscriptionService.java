package net.teleuptv.braintree.subscription.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.customer.repository.CustomerRepository;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.subscription.dto.CreateSubscriptionDTO;


@ApplicationScoped
public class SubscriptionService {

    private static final Logger LOG = Logger.getLogger(SubscriptionService.class);

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    CustomerRepository customerRepository;

    @ConfigProperty(name = "braintree.merchant-id")
    String merchantId;

    public Result<Subscription> createSubscription(CreateSubscriptionDTO dto){
        
        LOG.debug("Create a new subscription request in braintree");
        SubscriptionRequest request = new SubscriptionRequest()
            .paymentMethodNonce(dto.getPaymentMethodNonceFromClient())
            .planId(dto.getPlanId())
            .merchantAccountId(merchantId)                          //We do have options to update the billing address to the subscription here, if required, add it.
            .options()
                .startImmediately(true)
            .done();

        Result<Subscription> result = braintreeProvider.gateway().subscription().create(request);

        LOG.info("Subscription result: " + result.getMessage());
        LOG.debug("Subcription process completed.");
        return result;
    }

    public Result<Subscription> newCustomerPlanJourneyCreateSubscription(String paymentToken, String planId){
            SubscriptionRequest request = new SubscriptionRequest()
                    .paymentMethodToken(paymentToken)
                    .planId(planId);

            Result<Subscription> result = braintreeProvider.gateway().subscription().create(request);
            return result;
    }
}
