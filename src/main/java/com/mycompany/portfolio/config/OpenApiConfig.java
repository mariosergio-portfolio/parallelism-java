package com.mycompany.portfolio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI webstoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Parallelism API - JAVA")
                        .version("1.0.0"));
    }
}
