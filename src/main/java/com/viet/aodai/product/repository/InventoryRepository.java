package com.viet.aodai.product.repository;

import com.viet.aodai.product.domain.entity.Inventory;
import com.viet.aodai.product.domain.entity.Product;
import com.viet.aodai.product.repository.impl.InventoryRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long>, InventoryRepositoryImpl {

    @Query(
            value = """
                    SELECT * FROM inventory i
                    WHERE i.product_id = :productId
                    """,
            nativeQuery = true
    )
    Optional<Inventory> findInventoryByProduct(@Param("productId") Long productId);
}
