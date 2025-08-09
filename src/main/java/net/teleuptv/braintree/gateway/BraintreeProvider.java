package net.teleuptv.braintree.gateway;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class BraintreeProvider {

    private static final Logger LOG = Logger.getLogger(BraintreeProvider.class);

    private BraintreeGateway gateway;

    @ConfigProperty(name="braintree.environment")
    String environment;

    @ConfigProperty(name="braintree.merchant-id")
    String merchantId;

    @ConfigProperty(name="braintree.public-key")
    String publicKey;

    @ConfigProperty(name="braintree.private-key")
    String privateKey;

    void onStart(@Observes StartupEvent ev){
        Environment envEnum = getBraintreeEnvironment(environment);

        gateway = new BraintreeGateway(
            envEnum,
            merchantId,
            publicKey,
            privateKey
        );
        LOG.info("BraintreeGateway initialized with environment: " + envEnum);
    }

    private Environment getBraintreeEnvironment(String env) {
        switch (env.toUpperCase()) {
            case "SANDBOX":
                return Environment.SANDBOX;
            case "PRODUCTION":
                return Environment.PRODUCTION;
            case "DEVELOPMENT":
                return Environment.DEVELOPMENT;
            default:
                throw new IllegalArgumentException("Invalid Braintree environment: " + env);
        }
    }

    public BraintreeGateway gateway(){
        return gateway;
    }

    
}
