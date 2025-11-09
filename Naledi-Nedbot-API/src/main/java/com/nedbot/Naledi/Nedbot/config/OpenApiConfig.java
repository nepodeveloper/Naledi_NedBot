package com.nedbot.Naledi.Nedbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nedbot Banking API")
                        .description("API for banking operations including accounts, transactions, loans, and cards")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nedbot Team")
                                .email("support@nedbot.com")))
                .servers(List.of(
                        new Server().url("https://nedbot-app.azurewebsites.net").description("Production"),
                        new Server().url("http://localhost:8080").description("Development")
                ));
    }
}