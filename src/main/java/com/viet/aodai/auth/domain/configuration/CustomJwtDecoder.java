package com.viet.aodai.auth.domain.configuration;


import com.nimbusds.jose.JOSEException;
import com.viet.aodai.auth.domain.entity.RefreshToken;
import com.viet.aodai.auth.domain.security.JwtProperties;
import com.viet.aodai.auth.domain.security.JwtTokenProvider;
import com.viet.aodai.core.common.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.UserTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${spring.jwt.signerKey}")
    private String signerKey;

    private final JwtProperties jwtProperties;

    // volatile để đảm bảo visibility giữa các thread
    private volatile NimbusJwtDecoder jwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        // Double-checked locking – hiệu suất cao hơn synchronized method
        NimbusJwtDecoder localDecoder = jwtDecoder;
        if (localDecoder == null) {
            synchronized (this) {
                localDecoder = jwtDecoder;
                if (localDecoder == null) {
                    jwtDecoder = localDecoder = createDecoder();
                }
            }
        }
        // decode instance do hàm createDecoder tạo ra
        return localDecoder.decode(token);
    }

    private NimbusJwtDecoder createDecoder() {
        byte[] keyBytes = signerKey.getBytes(StandardCharsets.UTF_8);

        // Nên check độ dài key (HS512 yêu cầu ít nhất 512 bits = 64 bytes)
        if (keyBytes.length < 64) {
            throw new IllegalStateException("JWT signer key quá ngắn cho HS512 (cần ít nhất 64 bytes)");
        }

        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();

        // Chain validators
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(60)),  // clock skew 60s
                new JwtIssuerValidator(jwtProperties.getIssuer())
                // Thêm nếu cần: new AudienceValidator("your-api-audience"),
                // new JwtClaimValidator<>("roles", claims -> claims.contains("USER"))
        );

        decoder.setJwtValidator(validator);
        return decoder;
    }
}
