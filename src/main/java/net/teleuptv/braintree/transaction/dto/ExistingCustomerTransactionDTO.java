package net.teleuptv.braintree.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistingCustomerTransactionDTO {

    private String paymentMethodNonceFromClient;
    private BigDecimal amount;
    private String customerId;
}
