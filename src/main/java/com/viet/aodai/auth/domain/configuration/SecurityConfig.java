package com.viet.aodai.auth.domain.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static String[] PUBLIC_POST_ENDPOINTS = {""};
    private static String[] PUBLIC_GET_ENDPOINTS ={""};

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final CustomJwtDecoder customJwtDecoder;

    private final JwtConfigConverter jwtConfigConverter;

    public SecurityConfig(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CustomJwtDecoder customJwtDecoder,
            JwtConfigConverter jwtConfigConverter
    ) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customJwtDecoder = customJwtDecoder;
        this.jwtConfigConverter = jwtConfigConverter;
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors(cors -> {}) // Bật CORS cho Spring Security
                .csrf(AbstractHttpConfigurer::disable)
                // khong dung sesion de lưu trang thai dang nhap
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST,PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET,PUBLIC_GET_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());


        // xac thuc token tren tưng request
        // xac thuc token
        httpSecurity.oauth2ResourceServer(outh2 ->outh2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtConfigConverter.jwtAuthenticationConverter()))
                // bat loi 401 o tang ngoai
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );
        return httpSecurity.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}
