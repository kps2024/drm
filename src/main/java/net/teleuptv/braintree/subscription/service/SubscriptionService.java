package net.teleuptv.braintree.subscription.service;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.braintreegateway.Result;
import com.braintreegateway.Subscription;
import com.braintreegateway.SubscriptionRequest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.braintree.subscription.dto.CreateSubscriptionRequestDTO;

@ApplicationScoped
public class SubscriptionService {

    @Inject
    BraintreeProvider braintreeProvider;

    @ConfigProperty(name = "braintree.merchant-id")
    String merchantId;

    public Result<Subscription> create(CreateSubscriptionRequestDTO dto){
        //Braintree integration logic
        SubscriptionRequest request = new SubscriptionRequest()
            .paymentMethodToken(dto.getPaymentToken())  //use .payementMethodNonce method if you use client token
            .planId(dto.getPlanId())
            .merchantAccountId(merchantId); //should be get from application propertiers

        Result<Subscription> result = braintreeProvider.gateway().subscription().create(request);
        return result;
    }
}
