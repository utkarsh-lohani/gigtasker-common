package org.gigtasker.gigtaskercommon.config;

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
        final String securitySchemeName = "bearerAuth";
        final String oauth2SchemeName = "keycloakAuth";

        String authUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/auth";
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        return new OpenAPI()
                .servers(List.of(new Server().url(gatewayUrl).description("API Gateway")))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer").bearerFormat("JWT"))
                        // 2. ADD THIS: Native Keycloak Login
                        .addSecuritySchemes(oauth2SchemeName,
                            new SecurityScheme()
                            .type(SecurityScheme.Type.OAUTH2)
                            .description("Native Keycloak Login")
                            .flows(new OAuthFlows()
                                .authorizationCode(new OAuthFlow()
                                    .authorizationUrl(authUrl)
                                    .tokenUrl(tokenUrl)
                                    .scopes(new Scopes()
                                    .addString("openid", "OpenID Connect")
                                    .addString("profile", "User Profile"))
                                )
                            )
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(oauth2SchemeName));
    }
}
