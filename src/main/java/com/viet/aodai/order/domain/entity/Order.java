package com.viet.aodai.order.domain.entity;

import com.viet.aodai.order.domain.enumeration.OrderStatus;
import com.viet.aodai.order.domain.enumeration.OrderType;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.shipment.domain.entity.Shipment;
import com.viet.aodai.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(name = "order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", columnDefinition = "TEXT", nullable = false)
    private String shippingAddress;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "cancelled_by")
    private String cancelledBy; // USER, ADMIN, SYSTEM

    @Column(name = "cancel_date")
    private LocalDateTime cancelDate;

    @Column(name = "source_cart_id")
    private Long sourceCartId;

    @Column(name = "source_order_id")
    private Long sourceOrderId; // For reorder

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Shipment shipment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderHistory> history = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        orderNumber = generateOrderNumber();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(1000));
    }
}
