package com.viet.aodai.order.service;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.request.DirectOrderRequest;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;

import java.util.Map;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrderFromCart(UUID userId, OrderRequest request, String returnUrl);
    OrderResponse createDirectOrder(UUID userId, DirectOrderRequest request, String returnUrl);
    OrderResponse reOrder(UUID userId, Long sourceOrderId, String returnUrl, PaymentMethod paymentMethod);
    void confirmOrder(Long orderId, OrderStatus orderStatus, PaymentStatus paymentStatus, String note);
    void handlePaymentWebhook(Long paymentId, PaymentStatus paymentStatus);
    void cancelOrder(UUID userId, Long orderId, String notes);
}
