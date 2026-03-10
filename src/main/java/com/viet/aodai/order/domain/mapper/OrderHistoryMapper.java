package com.viet.aodai.order.domain.mapper;

import com.viet.aodai.order.domain.entity.OrderHistory;
import com.viet.aodai.order.domain.response.OrderHistoryResponse;
import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Builder
@Component
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
