package com.viet.aodai.payment.service;

import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.response.CallBackResult;
import org.springframework.data.redis.connection.stream.StreamInfo;

import java.util.Map;

public interface PaymentGatewayHandler {
    PaymentMethod supportedMethod();

    String initiate(Payment payment, String returnUrlBase, String ipnUrlBase);

    CallBackResult processWebhook(Map<String, String> params);
}
