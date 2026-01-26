package com.gigtasker.common.security;

import com.gigtasker.common.config.OpenApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.util.Optional;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@Import(OpenApiConfig.class)
public class GigTaskerSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            Optional<SecurityCustomizer> customizer,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter
    ) {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // Public Endpoints
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers("/actuator/**").permitAll()
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();

                    // Service-specific overrides
                    customizer.ifPresent(c -> c.customize(auth));

                    // Default: Authenticated
                    auth.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Explicitly use our custom converter (extracts Roles from Keycloak claim)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "jwtAuthenticationConverter")
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    @Bean
    @Primary
    public JwtDecoder jwtDecoder(
            // Use ':' to make these optional (prevent startup crash if missing)
            @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}") String jwkSetUri,
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}") String issuerUri
    ) {
        String effectiveUri = jwkSetUri;

        // Fallback: Derive JWK URI from Issuer URI if missing
        if (effectiveUri.isEmpty() && !issuerUri.isEmpty()) {
            effectiveUri = issuerUri + "/protocol/openid-connect/certs";
        }

        if (effectiveUri.isEmpty()) {
            throw new IllegalStateException("Security Config Error: No JWK Set URI or Issuer URI found.");
        }

        // 1. Build Decoder using the URL (fetches keys)
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(effectiveUri).build();

        // 2. Configure Relaxed Validation
        // Only validate timestamps (allow 60s skew). DO NOT validate Issuer string.
        // This allows Docker backend to accept tokens issued by 'localhost' browser session.
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator(Duration.ofSeconds(60));
        jwtDecoder.setJwtValidator(withTimestamp);

        return jwtDecoder;
    }
}
