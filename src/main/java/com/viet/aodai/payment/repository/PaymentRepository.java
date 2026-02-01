package com.viet.aodai.payment.repository;

import com.twilio.twiml.voice.Pay;
import com.viet.aodai.payment.domain.entity.Payment;
import com.viet.aodai.payment.repository.impl.PaymentRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, PaymentRepositoryImpl {
    @Query(
            value = """
                    SELECT * FROM payments p
                    WHERE p.order_id = :orderId
                    """,
            nativeQuery = true
    )
    Optional<Payment> findPaymentByOrderId(@Param("orderId") Long orderId);
}
