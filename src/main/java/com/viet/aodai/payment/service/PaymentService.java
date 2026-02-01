package com.viet.aodai.payment.service;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.domain.request.PaymentInitiateRequest;
import com.viet.aodai.payment.domain.response.PaymentInitiateResponse;

import java.util.Map;

public interface PaymentService {
    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request);
    void handleWebhook(String methodStr, Map<String, String> params);// methodStr để parse thành PaymentMethod
    void confirmPaymentManually(Long paymentId); // cho admin nếu webhook fail
    Payment findById(Long Id);

}
