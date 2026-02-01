package com.viet.aodai.product.domain.entity;

import com.viet.aodai.core.common.exception.AuthException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "inventory"
)
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    @Column(name = "quantity", nullable = false)
    @Min(0)
    private Integer quantity;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // Phương thức kiểm tra tồn kho
    public boolean hasEnoughStock(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }

    // trừ tồn kho
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
//            throw new InsufficientStockException(this.product.getId(), this.quantity, quantity);
            // sửa sau
            throw new AuthException("quantity not enough");
        }
        this.quantity -= quantity;
        this.updatedAt = LocalDateTime.now();
    }

    // cộng tồn kho (cho cancel order)
    public void increaseStock(int quantity) {
        this.quantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }
}
