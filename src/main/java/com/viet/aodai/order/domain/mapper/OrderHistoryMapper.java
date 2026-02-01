package com.viet.aodai.order.domain.mapper;

import com.viet.aodai.order.domain.entity.OrderHistory;
import com.viet.aodai.order.domain.response.OrderHistoryResponse;

public class OrderHistoryMapper {
    public static OrderHistoryResponse toOrderHistoryResponse(OrderHistory orderHistory){
        return OrderHistoryResponse.builder()
                .id(orderHistory.getId())
                .oldStatus(orderHistory.getOldStatus())
                .newStatus(orderHistory.getNewStatus())
                .notes(orderHistory.getNotes())
                .changedBy(orderHistory.getChangedBy())
                .createdAt(orderHistory.getCreatedAt())
                .build();
    }
}
