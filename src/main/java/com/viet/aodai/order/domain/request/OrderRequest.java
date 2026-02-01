package com.viet.aodai.order.domain.request;

import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    @NotBlank
    private String shippingAddress;

    private String billingAddress;

    private String notes;

    //front end set được client chọn
    @NotNull
    private PaymentMethod paymentMethod;
}
