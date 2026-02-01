package com.viet.aodai.order.domain.response;

import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.enumeration.OrderType;
import com.viet.aodai.payment.domain.response.PaymentResponse;
import com.viet.aodai.shipment.domain.response.ShipmentResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private OrderType orderType;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private LocalDateTime createdAt;

    private PaymentResponse paymentResponse;
    private ShipmentResponse shipmentResponse;
    private List<OrderItemResponse> items;
    private List<OrderHistoryResponse> history;
}
