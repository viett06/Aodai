package com.viet.aodai.order.domain.mapper;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.domain.response.UserResponse;

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
