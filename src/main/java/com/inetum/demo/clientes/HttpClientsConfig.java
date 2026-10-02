package com.inetum.demo.clientes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;

/** Los clientes HTTP de ejemplo: RestTemplate (bloqueante), WebClient (reactivo) y el HttpClient del JDK. */
@Configuration
public class HttpClientsConfig {

    @Bean
    public RestTemplate alumnosRestTemplate() {
        // El cliente por defecto (HttpURLConnection) no admite PATCH: se usa el HttpClient del JDK.
        return new RestTemplate(new JdkClientHttpRequestFactory());
    }

    @Bean
    public WebClient alumnosWebClient() {
        return WebClient.builder().build();
    }

    /** Cliente HTTP del propio JDK (java.net.http, sin dependencias de Spring): reutilizable y seguro entre hilos. */
    @Bean
    public HttpClient alumnosJdkHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }
}
