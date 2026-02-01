package com.viet.aodai.order.service.impl;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.entity.OrderHistory;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.repository.OrderHistoryRepository;
import com.viet.aodai.order.repository.OrderRepository;
import com.viet.aodai.order.service.OrderProcessor;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.user.domain.dto.UserRole;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProcessorImpl implements OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    @Transactional
    public Order processPaymentResult(Payment payment, PaymentStatus paymentStatus) {
        Order order = payment.getOrder();

        if (paymentStatus == PaymentStatus.COMPLETED){
            return handlePaymentSuccess(order, payment);
        }else if (paymentStatus == PaymentStatus.FAILED){
            return handlePaymentFailure(order, payment);
        }

        return order;
    }

    @Override
    @Transactional
    public Order handleImmediatePayment(Order order, Payment payment) {
        OrderHistory history = order.changeStatus(
                OrderStatus.PENDING,
                UserRole.CUSTOMER.name(),
                "Order created with " + payment.getPaymentMethod()
        );
        orderHistoryRepository.save(history);
        order.getHistory().add(history);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order handleRedirectPayment(Order order, Payment payment) {
        OrderHistory history = order.changeStatus(
                OrderStatus.PENDING,
                UserRole.CUSTOMER.name(),
                "Waiting for payment confirmation"
        );

        orderHistoryRepository.save(history);
        order.getHistory().add(history);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void confirmOrderManually(Long orderId, String notes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.PROCESSING_FINISH);

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(OrderStatus.PROCESSING_FINISH)
                .changedBy(UserRole.ADMIN.name())
                .notes(notes != null ? notes : "Manually confirmed by admin")
                .build();

        orderHistoryRepository.save(history);
        order.getHistory().add(history);

        orderRepository.save(order);
        log.info("Order {} manually confirmed by admin", orderId);

    }

    private Order handlePaymentSuccess(Order order, Payment payment){

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.PROCESSING_FINISH);

       OrderHistory history = OrderHistory.builder()
               .order(order)
               .oldStatus(oldStatus)
               .newStatus(OrderStatus.PROCESSING_FINISH)
               .changedBy(UserRole.SYSTEM.name())
               .notes("Payment completed via " + payment.getPaymentMethod())
               .build();
       orderHistoryRepository.save(history);
       order.getHistory().add(history);

        log.info("Payment completed for order: {}, payment: {}",
                order.getOrderNumber(), payment.getId());

        return orderRepository.save(order);
    }

    private Order handlePaymentFailure(Order order, Payment payment){

        OrderStatus oldStatus = order.getStatus();

        order.setStatus(OrderStatus.CANCELLED);

        OrderHistory history = OrderHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(OrderStatus.PROCESSING_FINISH)
                .changedBy(UserRole.SYSTEM.name())
                .notes("Payment failed via " + payment.getPaymentMethod())
                .build();

        log.warn("Payment failed for order: {}, payment: {}",
                order.getOrderNumber(), payment.getId());

        return orderRepository.save(order);
    }
}
