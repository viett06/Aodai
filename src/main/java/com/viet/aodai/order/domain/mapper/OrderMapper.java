package com.viet.aodai.order.domain.mapper;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.domain.response.UserResponse;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class OrderMapper {

    public static OrderResponse toOrderResponse(Order order){
       return OrderResponse.builder()
               .orderId(order.getId())
               .orderNumber(order.getOrderNumber())
               .orderType(order.getOrderType())
               .status(order.getStatus())
               .totalAmount(order.getTotalAmount())
               .shippingAddress(order.getShippingAddress())
               .billingAddress(order.getBillingAddress())
               .createdAt(order.getCreatedAt())
               .build();
    }
}
