package com.viet.aodai.payment.service.impl;

import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.response.CallBackResult;
import com.viet.aodai.payment.service.PaymentGatewayHandler;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BankTransferHandler implements PaymentGatewayHandler {

    private final RestTemplate restTemplate;

    @Value("${spring.vietqr.apiUrl}")
    private String vietQrApiUrl;
    @Value("${spring.vietqr.clientId}")
    private String clientId;
    @Value("${spring.vietqr.apiKey}")
    private String apiKey;
    @Value("${spring.vietqr.bankId}")
    private String bankId;
    @Value("${spring.vietqr.accountNo}")
    private String accountNo;
    @Value("${spring.vietqr.accountName}")
    private String accountName;


    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.BANK_TRANSFER;
    }

    @Override
    public String initiate(Payment payment, String returnUrlBase, String ipnUrlBase) {
        Map<String, Object> payload = Map.of(
                "accountNo", accountNo,
                "accountName", accountName,
                "accId",  bankId,
                "addInfo", "thanh toán đơn hàng" + payment.getOrder().getId(),
                "amount", payment.getAmount().intValue(),
                "template", "compact"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-xclient-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        Map response = restTemplate.postForObject(vietQrApiUrl, entity, Map.class);

        String qrUrl = (String) ((Map) response.get("data")).get("qrDataUrl");

        payment.setNoteContent("Thanh toán đơn hàng "+ payment.getOrder().getId());

        return qrUrl;

    }

    @Override
    public CallBackResult processWebhook(Map<String, String> params) {
        throw new UnsupportedOperationException("Bank Transfer không hỗ trợ webhook - dùng admin confirm");
    }
}
