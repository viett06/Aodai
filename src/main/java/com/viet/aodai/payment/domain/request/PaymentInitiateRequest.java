package com.viet.aodai.payment.domain.request;

import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {
    @NotNull
    private Long orderId;
    @NotNull
    private PaymentMethod paymentMethod;

    private String returnUrl; // Optional, frontend cung cấp nếu cần customize
}
