package net.teleuptv.braintree.customer.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import net.teleuptv.braintree.customer.model.DomainAppCustomer;

@ApplicationScoped
public class DomainAppCustomerRepository implements PanacheRepositoryBase<DomainAppCustomer, Long>{

}
