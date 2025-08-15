package net.teleuptv.braintree.plan.service;

import org.jboss.logging.Logger;

import com.braintreegateway.Plan;
import com.braintreegateway.exceptions.NotFoundException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.teleuptv.braintree.gateway.BraintreeProvider;


@ApplicationScoped
public class PlanService {

    private static final Logger LOG = Logger.getLogger(PlanService.class);

    @Inject
    BraintreeProvider braintreeProvider;

    //Find plan in braintree
    public Plan findPlan(String planId){
        try{
            LOG.info("Looking for the plan in braintree: " + planId);
            Plan plan = braintreeProvider.gateway().plan().find(planId);
            LOG.info("Plan found: "+ plan.getId());
            return plan;
        } catch(NotFoundException nf){
            LOG.error("No plan found in braintree, Exception occured: "+ nf);
            return null;
        }
    }
}
