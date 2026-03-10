package com.viet.aodai.auth.repository;

import com.viet.aodai.auth.domain.entity.InvalidateAccessToken;
import com.viet.aodai.auth.repository.custom.InvalidateAccessTokenRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvalidateAccessTokenRepository extends JpaRepository<InvalidateAccessToken, String>, InvalidateAccessTokenRepositoryCustom {
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
