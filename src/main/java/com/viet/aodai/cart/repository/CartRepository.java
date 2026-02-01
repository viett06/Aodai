package com.viet.aodai.cart.repository;

import com.viet.aodai.cart.domain.entity.Cart;
import com.viet.aodai.cart.repository.impl.CartRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryImpl {
    @Query(value = """
            SELECT * FROM cart c
            WHERE c.user_id = :userId
            """,
    nativeQuery = true)
    Optional<Cart> findCartByUserId(@Param("userId") UUID userId);
}
