package net.teleuptv.braintree.customer.service;

import org.jboss.logging.Logger;

import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.teleuptv.braintree.customer.dto.CreateCustomerDTO;
import net.teleuptv.braintree.customer.repository.CustomerRepository;
import net.teleuptv.braintree.gateway.BraintreeProvider;

@ApplicationScoped
public class CustomerService {

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    CustomerRepository customerRepository;

    private static final Logger LOG = Logger.getLogger(CustomerService.class);
    
    @Transactional
    public Result<Customer> CreateCustomer(CreateCustomerDTO dto) {

        //Creating new customer at Braintree
        LOG.info("Creating customer at braintree");
        LOG.debug("CreateCustomerDTO received : " + dto);
        CustomerRequest request = new CustomerRequest()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastname())
            .email(dto.getEmail())
            .phone(dto.getPhone());
        
        // Note : this is braintree customer object
        Result<Customer> result = braintreeProvider.gateway().customer().create(request);

        if(result.isSuccess()){
            String btCustomerId = result.getTarget().getId();
            LOG.info("Customer has been created at Braintree successfully. Customer Id :" + btCustomerId);
            LOG.info("Creating customer at Domain application");
            try {
                    //Creating new customer at Domain application
                    // Note : this is domain application customer object
                    net.teleuptv.braintree.customer.model.Customer customer = new net.teleuptv.braintree.customer.model.Customer();
                    customer.setBtCustomerId(btCustomerId);
                    customer.setFirstName(dto.getFirstName());
                    customer.setLastName(dto.getLastname());
                    customer.setEmail(dto.getEmail());
                    customer.setPhone(dto.getPhone());
                    customerRepository.persist(customer);
                    customerRepository.flush(); // Forces the INSERT now. Use flush() if you want the DB to confirm immediately.

                    LOG.info("Customer has been created at Domain application successfully. Customer Id :" + customer.getId());
                    return result;
            } catch(Exception e){
                LOG.error("Failed creating customer at Domain application. Exception occured: "+ e);
                return null;
            }            
        } else {
            return null;
        }

    }
}
