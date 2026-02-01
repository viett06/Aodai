package com.viet.aodai.payment.domain.mapper;

import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.response.PaymentResponse;

public class PaymentMapper {
    public static PaymentResponse toPaymentResponse(Payment payment){
       return PaymentResponse.builder()
                .id(payment.getId())
                .noteContent(payment.getNoteContent())
                .paymentMethod(payment.getPaymentMethod())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
