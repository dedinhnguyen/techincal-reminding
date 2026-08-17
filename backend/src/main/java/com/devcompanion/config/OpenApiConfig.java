package com.devcompanion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devCompanionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevCompanion (Fullstack CheatHub) API")
                        .description("Production-grade Architecture, Query Builder, and Cheatsheet Engine for Spring Boot 3.x and Angular 19+")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DevCompanion Core Team")
                                .email("architect@devcompanion.io")
                                .url("https://github.com/devcompanion/fullstack-cheathub"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server"),
                        new Server().url("http://backend:8080").description("Docker Compose Internal Gateway")
                ));
    }
}
