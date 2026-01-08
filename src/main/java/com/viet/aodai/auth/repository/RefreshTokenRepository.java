package com.viet.aodai.auth.repository;

import com.viet.aodai.auth.domain.entity.RefreshToken;
import com.viet.aodai.auth.repository.custom.RefreshTokenRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<UUID, RefreshToken>, RefreshTokenRepositoryCustom {
}
