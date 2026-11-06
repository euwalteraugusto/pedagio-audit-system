package com.pedagio.audit.audit_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${python.service.url}") String pythonServiceUrl) {
        // Criamos o builder e o client manualmente para evitar o erro de Bean não encontrado
        return WebClient.builder()
                .baseUrl(pythonServiceUrl)
                .build();
    }
}