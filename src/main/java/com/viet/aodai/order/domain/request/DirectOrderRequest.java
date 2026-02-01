package com.viet.aodai.order.domain.request;

import com.twilio.rest.api.v2010.account.call.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectOrderRequest {
    @NotNull
    private Long productId;

    @Min(1)
    @Max(20)
    private Integer quantity;

    @NotBlank
    private String shippingAddress;

    private String billingAddress;

    private String notes;

    @NotNull
    private PaymentMethod paymentMethod;
}
