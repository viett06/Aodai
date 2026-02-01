package com.viet.aodai.order.domain.response;

import com.viet.aodai.order.domain.enumeration.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistoryResponse {
    private Long id;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private String notes;
    private String changedBy;
    private LocalDateTime createdAt;
}
