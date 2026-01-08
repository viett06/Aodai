package com.viet.aodai.auth.domain.entity;

import com.viet.aodai.auth.domain.enumration.MfaType;
import com.viet.aodai.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mfa_otp")
@Builder
public class MfaOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mfaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    private MfaType type;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private int attemptCount;

    private boolean used;

    public int incrementAttempt(){
       return attemptCount = (attemptCount == 0) ? 1 : attemptCount + 1;
    }
}
