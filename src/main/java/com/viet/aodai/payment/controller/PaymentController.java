package com.viet.aodai.payment.controller;

import com.viet.aodai.payment.domain.request.PaymentInitiateRequest;
import com.viet.aodai.payment.domain.response.PaymentInitiateResponse;
import com.viet.aodai.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiate(@RequestBody @Valid PaymentInitiateRequest request){
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    @GetMapping("/return")
    public String handleReturn(@RequestParam Map<String, String> params) {
        // User redirect về đây sau thanh toán
        // Thường frontend xử lý, backend chỉ redirect về trang order với status
        String orderId = params.get("vnp_TxnRef") != null ? params.get("vnp_TxnRef") : params.get("orderId");
        return "redirect:/orders/" + orderId + "?payment_status=" + (params.get("vnp_ResponseCode") != null ? params.get("vnp_ResponseCode") : params.get("resultCode"));
    }

    @PostMapping("/webhook/{method}")
    public ResponseEntity<String> webhook(@PathVariable String method,
                                          @RequestParam Map<String, String> params) {
        paymentService.handleWebhook(method, params);
        return ResponseEntity.ok("OK");  // Trả OK cho gateway
    }

    // Cho admin confirm thủ công
    @PostMapping("/{paymentId}/confirm-manual")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> confirmManual(@PathVariable Long paymentId) {
        paymentService.confirmPaymentManually(paymentId);
        return ResponseEntity.ok("Payment confirmed manually");
    }
}
