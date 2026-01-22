package com.viet.aodai.auth.domain.entity;

import com.viet.aodai.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID refreshId;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "revoked")
    private boolean revoked;

    @Column(name = "revoke_reason")
    private String revokeReason;

    @Column(name = "revoke_at")
    private LocalDateTime revokeAt;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean isValid(){
        return !revoked && !isExpired();
    }

}
