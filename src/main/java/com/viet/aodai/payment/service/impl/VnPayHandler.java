package com.viet.aodai.payment.service.impl;

import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.domain.response.CallBackResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.viet.aodai.payment.service.PaymentGatewayHandler;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class VnPayHandler implements PaymentGatewayHandler {

    @Value("${spring.vnpay.url}")
    private String vnpayUrl;
    @Value("${spring.vnpay.tmnCode}")
    private String tmnCode;
    @Value("${spring.vnpay.hashSecret}")
    private String hashSecret;

    @Override
    public PaymentMethod supportedMethod() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public String initiate(Payment payment, String returnUrlBase, String ipnUrlBase) {
        // Build params theo doc VNPAY
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", payment.getId().toString());
        params.put("vnp_OrderInfo", "Thanh toan don hang " + payment.getOrder().getId());
        params.put("vnp_OrderType", "250000");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrlBase != null ? returnUrlBase : "https://yourdomain.com/api/payment/return");
        params.put("vnp_IpAddr", "127.0.0.1");  // Lấy từ request real nếu có
        params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // Tạo hash
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                query.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
                hashData.append(key).append('=').append(value).append('&');
            }
        }
        String hashDataStr = hashData.toString().substring(0, hashData.length() - 1);  // Xóa & cuối
        String secureHash = hmacSHA512(hashSecret, hashDataStr);

        String redirectUrl = vnpayUrl + "?" + query.toString() + "vnp_SecureHash=" + secureHash;

        return redirectUrl;
    }

    @Override
    public CallBackResult processWebhook(Map<String, String> params) {
        // Verify hash theo doc VNPAY
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHashType");  // Nếu có
        params.remove("vnp_SecureHash");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : new TreeMap<>(params).entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
        }
        String hashDataStr = hashData.toString().substring(0, hashData.length() - 1);
        String calculatedHash = hmacSHA512(hashSecret, hashDataStr);

        boolean isValid = calculatedHash.equals(vnpSecureHash);

        Long paymentId = Long.parseLong(params.get("vnp_TxnRef"));
        String responseCode = params.get("vnp_ResponseCode");
        PaymentStatus newStatus = "00".equals(responseCode) ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;

        return CallBackResult.builder()
                .paymentId(paymentId)
                .newStatus(newStatus)
                .transactionId(params.get("vnp_TransactionNo"))
                .reference(params.get("vnp_TxnRef"))
                .isValid(isValid)
                .build();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(secretKey);
            byte[] macData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : macData) {
                sb.append(Integer.toHexString((item & 0xff) | 0x100).substring(1));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC SHA512", e);
        }
    }
}
