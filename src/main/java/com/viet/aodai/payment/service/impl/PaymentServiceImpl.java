package com.viet.aodai.payment.service.impl;

import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.repository.OrderHistoryRepository;
import com.viet.aodai.order.repository.OrderRepository;
import com.viet.aodai.order.service.OrderService;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.domain.request.PaymentInitiateRequest;
import com.viet.aodai.payment.domain.response.CallBackResult;
import com.viet.aodai.payment.domain.response.PaymentInitiateResponse;
import com.viet.aodai.payment.repository.PaymentRepository;
import com.viet.aodai.payment.service.PaymentGatewayHandler;
import com.viet.aodai.payment.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService; // update order status
    private final OrderHistoryRepository orderHistoryRepository;
    private final Map<PaymentMethod, PaymentGatewayHandler> handlers;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderService orderService,
                              OrderRepository orderRepository,
                              OrderHistoryRepository orderHistoryRepository,
                              List<PaymentGatewayHandler> handlerList) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(PaymentGatewayHandler::supportedMethod, h -> h));
    }


    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(()-> new AuthException("order not found"));

        Payment payment = paymentRepository.findPaymentByOrderId(order.getId())
                .orElseThrow(()-> new AuthException("Payment not found for order"));

        // get handler for payment method
        PaymentGatewayHandler handler = handlers.get(request.getPaymentMethod());
        if (handler == null){
            throw new AuthException(
                    "Payment method not supported: " + request.getPaymentMethod());
        }
        // initiate payment with handler
        String url = handler.initiate(payment, request.getReturnUrl(), getIpnUrlForMethod(request.getPaymentMethod()));

        //Build response based on payment method
        PaymentInitiateResponse response = buildPaymentResponse(request.getPaymentMethod(), url, payment.getId());

        //Update payment with qr url for bank transfer
        if (request.getPaymentMethod() == PaymentMethod.BANK_TRANSFER && url != null){
            payment.setQrUrl(url);
            paymentRepository.save(payment);
        }

        log.info("Payment initiated: {} for order: {}",
                request.getPaymentMethod(), order.getOrderNumber());

        return response;

   }

    @Override
    @Transactional
    public void handleWebhook(String methodStr, Map<String, String> params) {
        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(methodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid webhook method: {}", methodStr);
            return;
        }

        PaymentGatewayHandler handler = handlers.get(method);
        if (handler == null) {
            log.warn("No handler for webhook method: {}", method);
            return;
        }

        // Process webhook with handler
        CallBackResult result = handler.processWebhook(params);
        if (!result.isValid()) {
            log.error("Invalid webhook for paymentId: {}, method: {}",
                    result.getPaymentId(), method);
            return;
        }

        Payment payment = findById(result.getPaymentId());


        // Prevent duplicate processing
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.info("Payment {} already processed", payment.getId());
            return;
        }

        payment.setTransactionId(result.getTransactionId());
        paymentRepository.save(payment);

        // Update payment status
        //updatePaymentFromWebhook(payment, result);

        // Notify order service about payment result
        orderService.handlePaymentWebhook(payment.getId(), result.getNewStatus());

        log.info("Webhook processed for payment: {}", payment.getId());
    }

    @Override
    @Transactional
    public void confirmPaymentManually(Long paymentId) {
        Payment payment = findById(paymentId);

        if (!canBeConfirmedManually(payment.getPaymentMethod())) {
            throw new UnsupportedOperationException(
                    "Manual confirmation not allowed for: " + payment.getPaymentMethod());
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // Notify order service
        orderService.handlePaymentWebhook(paymentId, PaymentStatus.COMPLETED);

        log.info("Payment {} manually confirmed by admin", paymentId);
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + id));
    }

    private String getIpnUrlForMethod(PaymentMethod method) {
        return "http://localhost:8080/api/payment/webhook/" + method.name().toLowerCase();  // Config động nếu cần
    }


    // bỏ cũng được
    private Order updatePaymentStatus(Long paymentId, OrderStatus orderStatus) {
        Order order = orderRepository.findOrderByPaymentId(paymentId)
                .orElseThrow(()-> new AuthException("Payment not found"));
        order.setStatus(orderStatus);
        orderRepository.save(order);
        return order;
    }

    private PaymentInitiateResponse buildPaymentResponse(PaymentMethod method,  String url, Long paymentId){
        PaymentInitiateResponse.PaymentInitiateResponseBuilder builder =
                PaymentInitiateResponse.builder()
                        .paymentId(paymentId);
        switch (method){
            case COD:
                return builder
                        .action("COD")
                        .message("Order created - Cash on Delivery")
                        .build();
            case BANK_TRANSFER:
                return builder
                        .action("BANK_TRANSFER")
                        .url(url)
                        .message("Scan QR code to transfer")
                        .build();

            case MOMO:
                return builder
                        .action("MOMO")
                        .url(url)
                        .message("Redirecting to MoMo payment")
                        .build();

            case VNPAY:
                return builder
                        .action("VN_PAY")
                        .url(url)
                        .message("Redirecting to VNPay payment")
                        .build();

            case PAYPAL:
                return builder
                        .action("PAYPAL")
                        .url(url)
                        .message("Redirecting to PayPal payment")
                        .build();

            default:
                throw new UnsupportedOperationException(
                        "Unsupported payment method: " + method);

        }
    }

    private void updatePaymentFromWebhook(Payment payment, CallBackResult result){
        payment.setStatus(result.getNewStatus());
        payment.setTransactionId(result.getTransactionId());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    private boolean canBeConfirmedManually(PaymentMethod method) {
        return method == PaymentMethod.COD || method == PaymentMethod.BANK_TRANSFER;
    }

}
