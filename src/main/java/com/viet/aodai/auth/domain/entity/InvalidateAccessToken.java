package com.viet.aodai.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "invalidate_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvalidateAccessToken {
    @Id
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiredAt;
}
