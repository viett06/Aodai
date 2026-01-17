package com.viet.aodai.auth.domain.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class JwtConfigConverter {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
         JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // Đổi tên claim từ default "scope" sang "role"
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

        // Đổi prefix từ "SCOPE_" sang "ROLE_"
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

        // Optional: Nếu muốn principal không phải từ "sub" mà từ claim khác
        // jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");

        return jwtAuthenticationConverter;
    }
}

