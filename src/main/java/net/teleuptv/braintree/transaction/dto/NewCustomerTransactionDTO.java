package net.teleuptv.braintree.transaction.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.teleuptv.braintree.customer.dto.CustomerDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCustomerTransactionDTO {

    private String paymentMethodNonceFromClient;
    private BigDecimal amount;
    private CustomerDTO customer;
}
