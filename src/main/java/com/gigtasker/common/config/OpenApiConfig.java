package com.gigtasker.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${gateway.url:http://localhost:9090}") String gatewayUrl,
            @Value("${keycloak.auth-server-url:http://localhost:8180}") String keycloakUrl,
            @Value("${keycloak.realm:gigtasker}") String realm) {

        final String BEARER_SCHEME = "bearerAuth";
        final String OAUTH2_SCHEME  = "keycloakAuth";

        final String authUrl  = "%s/realms/%s/protocol/openid-connect/auth".formatted(keycloakUrl, realm);
        final String tokenUrl = "%s/realms/%s/protocol/openid-connect/token".formatted(keycloakUrl, realm);

        // Bearer JWT Auth
        SecurityScheme bearerJwt = new SecurityScheme()
                .name(BEARER_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // OAuth2 Authorization Code (Keycloak Login)
        SecurityScheme oauth2 = new SecurityScheme()
                .name(OAUTH2_SCHEME)
                .type(SecurityScheme.Type.OAUTH2)
                .description("Keycloak OAuth2 Login")
                .flows(new OAuthFlows().authorizationCode(
                        new OAuthFlow()
                                .authorizationUrl(authUrl)
                                .tokenUrl(tokenUrl)
                                .scopes(new Scopes()
                                        .addString("openid", "OpenID Connect")
                                        .addString("profile", "User Profile")
                                )
                ));

        return new OpenAPI()
                .servers(List.of(
                        new Server().url(gatewayUrl).description("API Gateway")
                ))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, bearerJwt)
                        .addSecuritySchemes(OAUTH2_SCHEME, oauth2)
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(BEARER_SCHEME)
                        .addList(OAUTH2_SCHEME)
                );
    }
}
