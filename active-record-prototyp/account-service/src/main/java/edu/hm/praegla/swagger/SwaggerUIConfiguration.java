package edu.hm.praegla.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

@Configuration
public class SwaggerUIConfiguration {

    private String authUrl = "http://localhost:8080/oauth/";

    @Bean
    public OpenAPI sortTagsAlphabetically() {
        return new OpenAPI()
                .info(new Info()
                        .title("Account API")
                        .description("Documentation Account API v1.0"))
                .servers(Collections.singletonList(
                        new Server().url("http://localhost:8080/")
                ))
                .components(
                        new Components()
                                .addSecuritySchemes("OAuth2.0", new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("OAuth2 Flow")
                                        .flows(new OAuthFlows()
                                                .password(new OAuthFlow()
                                                        .authorizationUrl(authUrl + "authorize")
                                                        .refreshUrl(authUrl + "token")
                                                        .tokenUrl(authUrl + "token")
                                                        .scopes(new Scopes())
                                                ))))
                .security(Collections.singletonList(new SecurityRequirement().addList("OAuth2.0")));
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "PUT", "POST", "DELETE", "OPTION");
            }
        };
    }

}
