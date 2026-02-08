package com.viet.aodai.order.controller;


import com.viet.aodai.core.common.waraperApi.ApiResponse;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.request.DirectOrderRequest;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.order.service.OrderService;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Create order from cart
     * POST /api/orders/from-cart
     */
    @PostMapping("/from-cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderFromCart(
            @AuthenticationPrincipal UserDetails userDetails,  // Lấy từ JWT
            @Valid @RequestBody OrderRequest orderRequest,     // Add @Valid
            @RequestParam(required = false) String returnUrl   // Add @RequestParam
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        OrderResponse response = orderService.createOrderFromCart(
                userId,
                orderRequest,
                returnUrl
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    /**
     * Create direct order (buy now)
     * POST /api/orders/direct
     */
    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<OrderResponse>> createDirectOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DirectOrderRequest request,
            @RequestParam(required = false) String returnUrl
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        OrderResponse response = orderService.createDirectOrder(
                userId,
                request,
                returnUrl
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    /**
     * Reorder from existing order
     * POST /api/orders/{sourceOrderId}/reorder
     */
    @PostMapping("/{sourceOrderId}/reorder")
    public ResponseEntity<ApiResponse<OrderResponse>> reOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long sourceOrderId,
            @RequestParam(required = false) String returnUrl,
            @RequestParam PaymentMethod paymentMethod
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        OrderResponse response = orderService.reOrder(
                userId,
                sourceOrderId,
                returnUrl,
                paymentMethod
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order reordered successfully", response));
    }

    /**
     * Confirm order (ADMIN only)
     * PATCH /api/orders/{orderId}/confirm
     */
    @PatchMapping("/{orderId}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> confirmOrder(
            @PathVariable Long orderId,
            @RequestParam OrderStatus orderStatus,
            @RequestParam PaymentStatus paymentStatus,
            @RequestParam(required = false) String note
    ) {
        orderService.confirmOrder(orderId, orderStatus, paymentStatus, note);

        return ResponseEntity.ok(
                ApiResponse.success("Order confirmed successfully", null)
        );
    }

    /**
     * Cancel order (USER)
     * PATCH /api/orders/{orderId}/cancel
     */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestParam(required = false) String notes
    ) {
        UUID userId = UUID.fromString(userDetails.getUsername());

        orderService.cancelOrder(userId, orderId, notes);

        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled successfully", null)
        );
    }

//    /**
//     * Cancel order by admin (ADMIN only)
//     * PATCH /api/orders/{orderId}/cancel-by-admin
//     */
//    @PatchMapping("/{orderId}/cancel-by-admin")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<ApiResponse<Void>> cancelOrderByAdmin(
//            @AuthenticationPrincipal UserDetails adminDetails,
//            @PathVariable Long orderId,
//            @RequestParam String reason
//    ) {
//        orderService.cancelOrderByAdmin(orderId, adminDetails.getUsername(), reason);
//
//        return ResponseEntity.ok(
//                ApiResponse.success("Order cancelled by admin", null)
//        );
//    }
}