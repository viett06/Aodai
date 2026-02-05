package com.viet.aodai.order.controller;

import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.request.DirectOrderRequest;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.order.service.OrderService;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/fromcart")
    public OrderResponse createOrderFromCart(UUID userId, @RequestBody OrderRequest orderRequest, String returnUrl){
        return orderService.createOrderFromCart(userId, orderRequest, returnUrl);
    }

    @PostMapping("/direct")
    public OrderResponse createDirectOrder(UUID userId, @RequestBody DirectOrderRequest request, String returnUrl){
        return orderService.createDirectOrder(userId, request, returnUrl);
    }

    @PostMapping("/reorder")
    public OrderResponse reOrder(UUID userId, Long sourceOrderId, String returnUrl, PaymentMethod paymentMethod){
        return orderService.reOrder(userId, sourceOrderId, returnUrl, paymentMethod);
    }

    @PatchMapping("/confirm")
    public void confirmOrder(Long orderId, OrderStatus orderStatus, PaymentStatus paymentStatus, String note){
        orderService.confirmOrder(orderId, orderStatus, paymentStatus, note);
    }

}
