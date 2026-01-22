package com.viet.aodai.cart.domain.dto.response;

import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.enumeration.OrderType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private OrderType orderType;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private String notes;
    private String cancelReason;
    private String cancelledBy;
    private LocalDateTime cancelDate;
//    private List<OrderItemResponseDTO> items;
//    private PaymentResponseDTO payment;
//    private ShipmentResponseDTO shipment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
