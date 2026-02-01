package com.viet.aodai.order.service;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;

public interface OrderProcessor {
    Order processPaymentResult(Payment payment, PaymentStatus paymentStatus);

    Order handleImmediatePayment(Order order, Payment payment);

    Order handleRedirectPayment(Order order, Payment payment);

    void confirmOrderManually(Long orderId, String notes);
}
