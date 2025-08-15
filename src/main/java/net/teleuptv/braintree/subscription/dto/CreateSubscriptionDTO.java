package net.teleuptv.braintree.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionDTO {

    private String paymentMethodNonceFromClient;
    private String planId;
}
