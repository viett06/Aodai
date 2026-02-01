package com.viet.aodai.payment.domain.response;

import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallBackResult {
    private Long paymentId;
    private PaymentStatus newStatus;
    private String transactionId;
    private String reference;
    private boolean isValid;
}
