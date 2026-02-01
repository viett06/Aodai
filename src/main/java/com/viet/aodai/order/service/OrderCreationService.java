package com.viet.aodai.order.service;

import com.viet.aodai.cart.domain.entity.Cart;
import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.domain.request.OrderRequest;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.domain.enumeration.PaymentMethod;
import com.viet.aodai.user.domain.entity.User;

public interface OrderCreationService {
    Order createOrderFromCart(User user, Cart cart, OrderRequest orderRequest, String orderNumber);
    Payment createPayment(Order order, PaymentMethod paymentMethod);
}
