package com.viet.aodai.auth.repository;

import com.viet.aodai.auth.domain.entity.MfaOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MfaOtpRepository extends JpaRepository<MfaOtp, UUID> {
    @Query(
            value = """
                    SELECT * FROM mtp_otp
                    WHERE user_id = :userId
                    AND used = false
                    ORDER BY expired_at DESC
                    LIMIT 1
                    """, nativeQuery = true
    )
    Optional<MfaOtp> findTopByUserId(@Param("userId") UUID userId);

    @Query(
            value = """
        SELECT *
        FROM mtp_otp
        WHERE user_id = :userId
          AND used = false
          AND expired_at > NOW()
        ORDER BY created_at DESC
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<MfaOtp> findValidOtp(@Param("UserId") UUID userId);

    @Query(
            value = """
                    DELETE FROM mtp_otp
                    WHERE user_id = :userId
                    """,
            nativeQuery = true
    )
    void deleteExpiredOtps(@Param("userId") UUID userId);
}
