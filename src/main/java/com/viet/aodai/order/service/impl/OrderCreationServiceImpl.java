package com.viet.aodai.order.service.impl;

import com.viet.aodai.cart.domain.entity.Cart;
import com.viet.aodai.cart.domain.entity.CartItem;
import com.viet.aodai.cart.repository.CartRepository;
import com.viet.aodai.core.common.exception.AuthException;
import com.viet.aodai.core.common.exception.InsufficientStockException;
import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.entity.OrderItem;
import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.enumeration.OrderType;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.order.repository.OrderItemRepository;
import com.viet.aodai.order.repository.OrderRepository;
import com.viet.aodai.order.service.OrderCreationService;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.payment.domain.enumeration.PaymentStatus;
import com.viet.aodai.payment.repository.PaymentRepository;
import com.viet.aodai.product.domain.entity.Inventory;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.repository.InventoryRepository;
import com.viet.aodai.user.domain.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationServiceImpl implements OrderCreationService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;


    @Override
    @Transactional
    public Order createOrderFromCart(User user, Cart cart, OrderRequest orderRequest, String orderNumber) {
        validateStock(cart);

        Order order = createOrderEntity(user,cart, orderRequest, orderNumber);

        Order savedOrder = orderRepository.save(order);

        BigDecimal totalAmount = createOrderItemsAndUpdateInventory(cart, savedOrder);


        clearCart(cart);

        return savedOrder;
    }

    private void validateStock(Cart cart){
        for (CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            Inventory inventory = inventoryRepository.findInventoryByProduct(product.getId())
                    .orElseThrow(() -> new AuthException(
                            "Inventory not found for product: " + product.getId()));
            if (!inventory.hasEnoughStock(cartItem.getQuantity())){
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName(),
                        product.getId(),
                        inventory.getQuantity(),
                        cartItem.getQuantity()
                );
            }
        }
    }

    private Order createOrderEntity(User user, Cart cart, OrderRequest request, String orderNumber){
        return Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .orderType(OrderType.FROM_CART)
                .status(OrderStatus.PENDING)
                .sourceCartId(cart.getId())
                .shippingAddress(request.getShippingAddress())
                .billingAddress(request.getBillingAddress())
                .notes(request.getNotes())
                .items(new HashSet<>())
                .history(new HashSet<>())
                .build();
    }

    private BigDecimal createOrderItemsAndUpdateInventory(Cart cart, Order order){
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(quantity)));

            Inventory inventory = inventoryRepository.findInventoryByProduct(product.getId())
                    .orElseThrow(() -> new AuthException("Inventory not found"));
            inventory.decreaseStock(quantity);
            inventoryRepository.save(inventory);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(quantity)
                    .unitPrice(cartItem.getPrice())
                    .totalPrice(cartItem.getTotalPrice())
                    .build();
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        }
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

       return totalAmount;
    }

    private void clearCart(Cart cart){
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Payment createPayment(Order order, PaymentMethod paymentMethod) {
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }
}
