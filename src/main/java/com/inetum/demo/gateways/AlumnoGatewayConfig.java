package com.inetum.demo.gateways;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(AlumnoGatewayProperties.class)
public class AlumnoGatewayConfig {

    /** RestClient (cliente HTTP bloqueante moderno) con timeouts de conexión y de lectura. */
    @Bean
    public RestClient alumnoGatewayRestClient(AlumnoGatewayProperties props) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(props.connectTimeout()).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(props.readTimeout());
        return RestClient.builder().requestFactory(factory).build();
    }

    /**
     * Reintentos con backoff exponencial. Solo se consideran transitorios los fallos de red o
     * timeout (ResourceAccessException) y las respuestas 5xx; un 4xx no mejora por reintentar.
     */
    @Bean
    public RetryTemplate alumnoGatewayRetryTemplate(AlumnoGatewayProperties props) {
        RetryPolicy policy = RetryPolicy.builder()
                .includes(ResourceAccessException.class, HttpServerErrorException.class)
                .maxRetries(props.maxRetries())
                .delay(props.delay())
                .multiplier(props.multiplier())
                .maxDelay(props.maxDelay())
                .jitter(props.jitter())
                .build();
        return new RetryTemplate(policy);
    }
}
