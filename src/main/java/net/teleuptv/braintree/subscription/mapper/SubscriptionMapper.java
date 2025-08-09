package net.teleuptv.braintree.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.braintreegateway.SubscriptionRequest;

import net.teleuptv.braintree.subscription.dto.CreateSubscriptionRequestDTO;

@Mapper
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(source = "paymentToken", target = "paymentMethodToken")
    @Mapping(source = "planId", target = "planId")
    SubscriptionRequest toSubscriptionRequest(CreateSubscriptionRequestDTO requestDTO);


}
