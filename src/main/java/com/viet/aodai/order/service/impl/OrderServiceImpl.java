package com.viet.aodai.order.service.impl;

import com.viet.aodai.cart.domain.entity.Cart;
import com.viet.aodai.cart.domain.entity.CartItem;
import com.viet.aodai.cart.repository.CartRepository;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.core.common.exception.InsufficientStockException;
import com.viet.aodai.core.common.exception.UserNotFoundException;
import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.entity.OrderHistory;
import com.viet.aodai.order.domain.entity.OrderItem;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.enumeration.OrderType;
import com.viet.aodai.order.domain.mapper.OrderHistoryMapper;
import com.viet.aodai.order.domain.mapper.OrderItemMapper;
import com.viet.aodai.order.domain.mapper.OrderMapper;
import com.viet.aodai.order.domain.request.DirectOrderRequest;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.order.domain.response.OrderResponse;
import com.viet.aodai.order.repository.OrderHistoryRepository;
import com.viet.aodai.order.repository.OrderItemRepository;
import com.viet.aodai.order.repository.OrderRepository;
import com.viet.aodai.order.service.OrderCreationService;
import com.viet.aodai.order.service.OrderProcessor;
import com.viet.aodai.order.service.OrderService;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.domain.mapper.PaymentMapper;
import com.viet.aodai.payment.domain.request.PaymentInitiateRequest;
import com.viet.aodai.payment.domain.response.PaymentInitiateResponse;
import com.viet.aodai.payment.repository.PaymentRepository;
import com.viet.aodai.payment.service.PaymentService;
import com.viet.aodai.product.domain.entity.Inventory;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.repository.InventoryRepository;
import com.viet.aodai.user.domain.dto.UserRole;
import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderCreationService orderCreationService;
    private final OrderProcessor orderProcessor;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public OrderResponse createOrderFromCart(UUID userId, OrderRequest request, String returnUrl) {
        log.info("Creating order from cart for user: {}", userId);

        // 1. Validate user
        User user = validateUser(userId);

        // 2. Get and validate cart
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseThrow(() -> new AuthException("Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new AuthException("Cart is empty");
        }

        // 3. Generate order number
        String orderNumber = generateOrderNumber();

        // 4. Create order with all items
        Order order = orderCreationService.createOrderFromCart(user, cart, request, orderNumber);

        // 5. Create payment record
        Payment payment = orderCreationService.createPayment(order, request.getPaymentMethod());
        order.setPayment(payment);

        // 6. Initialize payment with payment service
        // nếu lad các phương thức external payment thì sẽ được webhook xử lý handleWebhook luôn sau khi xong order không cần xử lý internal backend
        PaymentInitiateResponse paymentInitiateResponse;
        try {
            paymentInitiateResponse = initializePayment(payment, returnUrl);
        } catch (Exception e) {
            log.error("Payment initiation failed for order: {}", orderNumber, e);
            //Rollback transaction
            throw new AuthException("Payment initiation failed: " + e.getMessage());
        }
        // 7. Process payment based on type
        processPaymentBasedOnType(order, payment, paymentInitiateResponse);

        // 8. Build and return response
        return buildOrderResponse(order);

    }

    @Override
    @Transactional
    public OrderResponse createDirectOrder(UUID userId, DirectOrderRequest request, String returnUrl) {

        User user = validateUser(userId);

        String orderNumber = generateOrderNumber();

        Order order = orderCreationService.createDirectOrder(user, request, orderNumber);

        Payment payment = orderCreationService.createPayment(order, request.getPaymentMethod());
        order.setPayment(payment);

        PaymentInitiateResponse paymentInitiateResponse;
        try {
            paymentInitiateResponse = initializePayment(payment, returnUrl);
        } catch (Exception e) {
            log.error("Payment initiation failed for order: {}", orderNumber, e);
            //Rollback transaction
            throw new AuthException("Payment initiation failed: " + e.getMessage());
        }

        processPaymentBasedOnType(order, payment, paymentInitiateResponse);

        return buildOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse reOrder(UUID userId, Long sourceOrderId, String returnUrl, PaymentMethod paymentMethod) {
        User user = validateUser(userId);

        String orderNumber = generateOrderNumber();

        Order order = orderCreationService.reOrder(user, sourceOrderId, orderNumber);

        Payment payment = orderCreationService.createPayment(order, paymentMethod);
        order.setPayment(payment);

        PaymentInitiateResponse paymentInitiateResponse;
        try {
            paymentInitiateResponse = initializePayment(payment, returnUrl);
        } catch (Exception e) {
            log.error("Payment initiation failed for order: {}", orderNumber, e);
            //Rollback transaction
            throw new AuthException("Payment initiation failed: " + e.getMessage());
        }

        processPaymentBasedOnType(order, payment, paymentInitiateResponse);


        return buildOrderResponse(order);



    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId,OrderStatus newOrderStatus, PaymentStatus newPaymentStatus, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AuthException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot confirm cancelled order");
        }

        if (order.getStatus() == OrderStatus.PROCESSING_FINISH) {
            throw new IllegalStateException("Order already completed");
        }


        // Update payment status
        Payment payment = paymentRepository.findPaymentByOrderId(orderId)
                .orElseThrow(() -> new AuthException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(newPaymentStatus);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
        }


        if (newOrderStatus == OrderStatus.PROCESSING_FINISH) {
            orderProcessor.confirmOrderManually(orderId, note);
        } else {
            // Handle other status changes
            OrderHistory history = order.changeStatus(newOrderStatus, "ADMIN", note);
            orderHistoryRepository.save(history);
            orderRepository.save(order);
        }
        log.info("Order {} confirmed with status {} and payment status {}",
                orderId, newOrderStatus, newPaymentStatus);
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(Long paymentId, PaymentStatus newPaymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AuthException("Payment not found"));

        orderProcessor.processPaymentResult(payment, newPaymentStatus);

        log.info("Payment webhook processed: {} -> {}", paymentId, newPaymentStatus);
    }

    private User validateUser(UUID userId){
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!user.isEnable()) {
            throw new AuthException("User account is not active");
        }

        if (!user.isAccountNonLocked()) {
            throw new AuthException("User account is locked");
        }

        return user;
    }

    private OrderResponse buildOrderResponse(Order order) {
        OrderResponse response = OrderMapper.toOrderResponse(order);

        // Set payment info
        if (order.getPayment() != null) {
            response.setPaymentResponse(PaymentMapper.toPaymentResponse(order.getPayment()));
        }

        // Set order items
        response.setItems(order.getItems().stream()
                .map(OrderItemMapper::toOrderItemResponse)
                .toList());

        // Set latest history
        order.getHistory().stream()
                .max(Comparator.comparing(OrderHistory::getCreatedAt))
                .ifPresent(latestHistory ->
                        response.setHistory(List.of(
                                OrderHistoryMapper.toOrderHistoryResponse(latestHistory)
                        )));

        return response;
    }


    private String generateOrderNumber(){
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(1000));
     }

     private record OrderItemData(Product product, Integer quantity,
                                  BigDecimal unitPrice, BigDecimal totalPrice){};


    private PaymentInitiateResponse initializePayment(Payment payment, String returnUrl) {
        Order order = payment.getOrder();
        if (order == null) {
            throw new IllegalStateException("Payment has no associated order");
        }

        PaymentInitiateRequest paymentRequest = PaymentInitiateRequest.builder()
                .orderId(order.getId())
                .paymentMethod(payment.getPaymentMethod())
                .returnUrl(returnUrl)
                .build();

        return paymentService.initiatePayment(paymentRequest);
    }

    private void processPaymentBasedOnType(Order order, Payment payment,
                                           PaymentInitiateResponse paymentResponse) {
        PaymentMethod paymentMethod = payment.getPaymentMethod();

        // Handle immediate payments (COD, BANK_TRANSFER)
        if (isImmediatePayment(paymentMethod)) {
            orderProcessor.handleImmediatePayment(order, payment);

            // For BANK_TRANSFER, set QR URL
            if (paymentMethod == PaymentMethod.BANK_TRANSFER) {
                // bỏ cũng được
                payment.setQrUrl(paymentResponse.getUrl());
                paymentRepository.save(payment);
            }
        }
        // Handle redirect payments
        else {
            //orderProcessor.handleRedirectPayment(order, payment);

            // Process payment gateway if needed
            //processPaymentGateway(paymentResponse.getAction());

            orderProcessor.handleRedirectPayment(order, payment);

            // xư lý case khi người dùng bỏ giở thanh toán
            log.info("Redirect payment initiated for order: {}, waiting for webhook",
                    order.getOrderNumber());
        }

        // Save final state
        orderRepository.save(order);
    }

    private boolean isImmediatePayment(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.COD ||
                paymentMethod == PaymentMethod.BANK_TRANSFER;
    }

}
