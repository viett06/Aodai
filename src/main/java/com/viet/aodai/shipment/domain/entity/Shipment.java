package com.viet.aodai.shipment.domain.entity;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.shipment.domain.enumeration.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "shipping_method", nullable = false)
    private String shippingMethod;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Column(name = "estimated_delivery")
    private LocalDate estimatedDelivery;

    @Column(name = "actual_delivery")
    private LocalDate actualDelivery;

    @Column(name = "shipping_address", columnDefinition = "TEXT", nullable = false)
    private String shippingAddress;

    @Column(name = "shipping_cost", nullable = false)
    private BigDecimal shippingCost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        trackingNumber = "TRACK" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
