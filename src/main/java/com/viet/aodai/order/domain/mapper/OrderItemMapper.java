package com.viet.aodai.order.domain.mapper;

import com.viet.aodai.order.domain.entity.OrderItem;
import com.viet.aodai.order.domain.response.OrderItemResponse;

public class OrderItemMapper {
    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem){
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
