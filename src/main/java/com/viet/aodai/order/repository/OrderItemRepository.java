package com.viet.aodai.order.repository;

import com.viet.aodai.order.domain.entity.OrderItem;
import com.viet.aodai.order.repository.impl.OrderItemRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, OrderItemRepositoryImpl {
}
