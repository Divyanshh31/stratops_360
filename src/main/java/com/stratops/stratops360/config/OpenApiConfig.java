package com.stratops.stratops360.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stratopsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("StratOps 360 MIS API")
                        .description("REST API layer for Enterprise Balanced Scorecard, Herbert Simon Decision Pipeline, What-If DSS Simulation, and Forward-Chaining Expert System.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("StratOps 360 Engineering")
                                .email("info@stratops360.example.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
