package com.pedagio.audit.audit_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        // Criamos o builder e o client manualmente para evitar o erro de Bean não encontrado
        return WebClient.builder()
                .baseUrl("http://127.0.0.1:8000")
                .build();
    }
}