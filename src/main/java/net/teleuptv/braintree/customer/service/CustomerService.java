package net.teleuptv.braintree.customer.service;

import java.util.ArrayList;

import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Subscription;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import net.teleuptv.braintree.customer.dto.CreateCustomerDTO;
import net.teleuptv.braintree.customer.dto.NewCustomerPlanJourney;
import net.teleuptv.braintree.customer.model.DomainAppCustomer;
import net.teleuptv.braintree.customer.repository.CustomerRepository;
import net.teleuptv.braintree.customer.repository.DomainAppCustomerRepository;
import net.teleuptv.braintree.gateway.BraintreeProvider;
import net.teleuptv.common.AppResponse;

@ApplicationScoped
public class CustomerService {

    @Inject
    BraintreeProvider braintreeProvider;

    @Inject
    CustomerRepository customerRepository;

    @Inject
    DomainAppCustomerRepository domainAppCustomerRepository;

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

    public Result<Customer> newCustomerPlanJourneyCreateCustomer(NewCustomerPlanJourney dto){
        CustomerRequest request = new CustomerRequest()
                    .email(dto.getEmail())
                    .paymentMethodNonce(dto.getPaymentMethodNonce());
                
                Result<Customer> result = braintreeProvider.gateway().customer().create(request);
                
                return result;         
    }

    @Transactional
    public void createCustomerInDomainApp(NewCustomerPlanJourney dto, Result<Customer> customerResult, Result<Subscription> subscriptionResult, String trackingId){
                
                MDC.put("trackingId", trackingId);

                DomainAppCustomer dACustomer = new DomainAppCustomer();
                dACustomer.setBtCustomerId(customerResult.getTarget().getId());
                dACustomer.setEmail(dto.getEmail());

                if(dACustomer.getBtSubscriptionId() == null){
                    dACustomer.setBtSubscriptionId(new ArrayList<>());
                }
                dACustomer.getBtSubscriptionId().add(subscriptionResult.getTarget().getId());

                if(dACustomer.getBtPlanId() == null){
                    dACustomer.setBtPlanId(new ArrayList<>());
                }
                dACustomer.getBtPlanId().add(dto.getPlanId());
                domainAppCustomerRepository.persist(dACustomer); 
                
                LOG.info("Customer details has been upated in the domain app customer model " + dACustomer.getId());
                MDC.clear();
                
    }
}
