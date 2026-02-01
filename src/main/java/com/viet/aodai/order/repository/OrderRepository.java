package com.viet.aodai.order.repository;

import com.viet.aodai.order.domain.entity.Order;
import com.viet.aodai.order.repository.impl.OrderRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>, OrderRepositoryImpl {
    @Query(
            value = """
                 SELECT * FROM orders
                 ORDER BY creted_at DESC
                 LIMIT 1
                  """,
            nativeQuery = true
    )
    Optional<Order> findOrderByCreated_at();

    @Query(
            value = """
                    SELECT * FROM orders o
                    WHERE o.payment = :paymentId
                    """,
            nativeQuery = true
    )
    Optional<Order> findOrderByPaymentId(@Param("paymentId") Long paymentId);
}
