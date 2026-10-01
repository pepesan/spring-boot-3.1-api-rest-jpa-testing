package com.inetum.demo.clientes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/** Los dos clientes HTTP de ejemplo: el bloqueante (RestTemplate) y el reactivo (WebClient). */
@Configuration
public class HttpClientsConfig {

    @Bean
    public RestTemplate alumnosRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient alumnosWebClient() {
        return WebClient.builder().build();
    }
}
