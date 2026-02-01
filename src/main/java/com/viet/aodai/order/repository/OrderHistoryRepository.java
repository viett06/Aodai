package com.viet.aodai.order.repository;

import com.viet.aodai.order.domain.entity.OrderHistory;
import com.viet.aodai.order.repository.impl.OrderHistoryRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long>, OrderHistoryRepositoryImpl {
}
