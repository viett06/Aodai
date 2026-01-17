package com.viet.aodai.auth.domain.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.print.event.PrintJobAttributeEvent;
import java.awt.*;
import java.security.PrivateKey;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
@Validated
public class JwtProperties {



    @NotBlank
    private String issuer = "ao-dai-rental";

    @NotNull
    @Min(60000) //1 minute
    private Long accessTokenExpiration = 900000L; // 15 minute

    @NotNull
    @Min(86400000)
    private Long refreshTokenExpiration = 604800000L; //7 days

    @NotNull
    @Min(300000)
    private Long emailVerificationExpỉation = 86400000L; // 24 hours

    @NotNull
    @Min(300000) // 5 minutes
    private Long passwordResetExpiration = 3600000L; // 1 hour


}
