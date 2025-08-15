package net.teleuptv.braintree.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.braintreegateway.SubscriptionRequest;

import net.teleuptv.braintree.subscription.dto.CreateSubscriptionDTO;

@Mapper
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(source = "paymentMethodNonceFromClient", target = "paymentMethodNonceFromClient")
    @Mapping(source = "planId", target = "planId")
    SubscriptionRequest toSubscriptionRequest(CreateSubscriptionDTO requestDTO);


}
