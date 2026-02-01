package com.viet.aodai.payment.service.impl;

import com.nimbusds.jose.crypto.impl.HMAC;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.domain.response.CallBackResult;
import com.viet.aodai.payment.service.PaymentGatewayHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MomoHandler implements PaymentGatewayHandler {
    private final RestTemplate restTemplate;

    @Value("${spring.momo.endpoint}")
    private String endpoint;
    @Value("${spring.momo.partnerCode}")
    private String partnerCode;
    @Value("${spring.momo.accessKey}")
    private String accessKey;
    @Value("${spring.momo.secretKey}")
    private String secretKey;

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.MOMO;
    }

    @Override
    public String initiate(Payment payment, String returnUrlBase, String ipnUrlBase) {
        // Build payload theo doc MoMo (v2)
        String requestId = UUID.randomUUID().toString();
        String orderId = payment.getId().toString();
        Long amount = payment.getAmount().longValue();

        String rawSignature = "accessKey" + accessKey + "&amount=" + amount + "&extraData=" + "" +"&ipnUrl=" + ipnUrlBase +
                "&orderId=" + orderId + "&orderInfo=Thanh toan don hang " + payment.getOrder().getId() +
                "&partnerCode=" + partnerCode + "&redirectUrl=" + returnUrlBase + "&requestId=" + requestId +
                "&requestType=captureWallet";

        String signature = hmacSHA256(secretKey,rawSignature);

        Map<String, Object> payload = new HashMap<>();
        payload.put("partnerCode", partnerCode);
        payload.put("partnerName", "Your Shop Name");
        payload.put("storeId", partnerCode);
        payload.put("requestType", "captureWallet");
        payload.put("ipnUrl", ipnUrlBase);
        payload.put("redirectUrl", returnUrlBase);
        payload.put("orderId", orderId);
        payload.put("amount", amount);
        payload.put("lang", "vi");
        payload.put("orderInfo", "Thanh toan don hang " + payment.getOrder().getId());
        payload.put("requestId", requestId);
        payload.put("extraData", "");
        payload.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        Map response = restTemplate.postForObject(endpoint, entity, Map.class);

        return (String) response.get("payUrl");  // Redirect URL từ MoMo

    }

    @Override
    public CallBackResult processWebhook(Map<String, String> params) {
        // Verify signature theo doc MoMo
        String rawSignature = "accessKey=" + accessKey + "&amount=" + params.get("amount") + "&extraData=" + params.get("extraData") +
                "&message=" + params.get("message") + "&orderId=" + params.get("orderId") + "&orderInfo=" + params.get("orderInfo") +
                "&orderType=" + params.get("orderType") + "&partnerCode=" + params.get("partnerCode") + "&payType=" + params.get("payType") +
                "&requestId=" + params.get("requestId") + "&responseTime=" + params.get("responseTime") + "&resultCode=" + params.get("resultCode") +
                "&transId=" + params.get("transId");

        String calculatedSignature = hmacSHA256(secretKey, rawSignature);

        boolean isValid = calculatedSignature.equals(params.get("signature"));

        Long paymentId = Long.parseLong(params.get("orderId"));

        String resultCode = params.get("resultCode");

        PaymentStatus newStatus = "0".equals(resultCode) ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        return CallBackResult.builder()
                .paymentId(paymentId)
                .newStatus(newStatus)
                .transactionId(params.get("transId"))
                .reference(params.get("orderId"))
                .isValid(isValid)
                .build();
    }

    private String hmacSHA256(String key, String data){
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] macData = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : macData) {
                sb.append(Integer.toHexString((item & 0xff) | 0x100).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC SHA256", e);
        }
    }
}
