package com.viet.aodai.payment.domain.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInitiateResponse {
    private String action;
    private String url;
    private String message;
    private Long paymentId;
}
