package com.viet.aodai.payment.service.impl;

import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.response.CallBackResult;
import com.viet.aodai.payment.service.PaymentGatewayHandler;

import java.util.Map;

public class CodHandler implements PaymentGatewayHandler {
    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.COD;
    }

    @Override
    public String initiate(Payment payment, String returnUrlBase, String ipnUrlBase) {
        return null; // không cần url
    }

    @Override
    public CallBackResult processWebhook(Map<String, String> params) {
        throw new UnsupportedOperationException("COD don't support webhook");
    }
}
