package net.teleuptv.braintree.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequestDTO {

    private String paymentToken;
    private String planId;
    private String merchantAccountId;

}
