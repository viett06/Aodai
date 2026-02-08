package com.viet.aodai.auth.repository;

import com.viet.aodai.auth.domain.entity.InvalidateAccessToken;
import com.viet.aodai.auth.repository.custom.InvalidateAccessTokenRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InvalidateAccessTokenRepository extends JpaRepository<String, InvalidateAccessToken>, InvalidateAccessTokenRepositoryCustom {
    @Query(
            value = """
                    INSERT INTO invalidate_tokens(token)
                    VALUES (:token)
                    """,
            nativeQuery = true
    )
    void saveInvalidToken(@Param("token") String token);

    @Query(
            value = """
                    SELECT EXISTS (
                    SELECT 1
                    FROM invalidate_tokens
                    WHERE token = :refreshToken
                    )
                    """,
            nativeQuery = true
    )
    boolean isTokenInvalidated(@Param("refreshToken") String refreshToken);
}
