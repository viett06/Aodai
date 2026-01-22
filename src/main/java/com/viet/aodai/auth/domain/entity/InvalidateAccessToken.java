package com.viet.aodai.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invalidate_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvalidateAccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String token;
}
