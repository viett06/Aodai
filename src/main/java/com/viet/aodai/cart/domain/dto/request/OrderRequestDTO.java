package com.viet.aodai.cart.domain.dto.request;

import com.viet.aodai.order.domain.enumeration.OrderType;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long userId;
    private OrderType orderType;

    // For FROM_CART order
    private Long cartId;

    // For BUY_NOW order
//    private List<OrderItemRequestDTO> buyNowItems;

    // For REORDER
    private Long sourceOrderId;

    private String shippingAddress;
    private String billingAddress;
    private String notes;

    @NotNull
    private PaymentMethod paymentMethod;

    @NotNull
    private String shippingMethod;

    // For QUICK_CHECKOUT (pre-filled info)
    private boolean useSavedAddress;
    private boolean useSavedPayment;
}
