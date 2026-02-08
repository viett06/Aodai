package com.viet.aodai.user.repository;

import com.viet.aodai.user.domain.entity.User;
import com.viet.aodai.user.domain.response.UserResponse;
import com.viet.aodai.user.repository.custom.UserRepositoryCustom;
import jakarta.persistence.criteria.From;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    @Query(
            value = "SELECT * FROM users WHERE user_id = :userId",
            nativeQuery = true
    )
    Optional<User> findUserById(@Param("userId") UUID userId);

    @Query(
            value = "SELECT * FROM users WHERE username = :username",
            nativeQuery = true
    )
    Optional<User> findUserByUsername(@Param("username") String username);

    @Query(
            value = """
                    SELECT * FROM users AS u
                    WHERE u.email = :email
                    """,
            nativeQuery = true
    )
    Optional<User> findUserByEmail(@Param("email") String email);


    @Query(
            value = """
                    UPDATE users
                    SET password_hash = :hashPassword
                    WHERE user_id = :userId
                    """,
            nativeQuery = true
    )
    void updatePassword(@Param("userId") UUID userId,@Param("hashPassword") String hashPassword);
}
