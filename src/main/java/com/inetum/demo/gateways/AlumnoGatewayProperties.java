package com.inetum.demo.gateways;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/**
 * Configuración del gateway ({@code alumnos.gateway.*}). El host y puerto salen de
 * {@link com.inetum.demo.clientes.AlumnoApiBaseUrl}; aquí solo la ruta, los timeouts y los reintentos.
 *
 * @param path          ruta del recurso remoto
 * @param maxRetries    reintentos tras el primer intento (3 reintentos = hasta 4 llamadas)
 * @param delay         espera antes del primer reintento
 * @param multiplier    factor de crecimiento de la espera entre reintentos (backoff exponencial)
 * @param maxDelay      tope de la espera entre reintentos
 * @param jitter        variación aleatoria de la espera, para que varios clientes no reintenten a la vez
 * @param retryAfter    valor de la cabecera Retry-After que se envía al cliente al dar 503
 */
@ConfigurationProperties("alumnos.gateway")
public record AlumnoGatewayProperties(
        @DefaultValue("/api/v1/alumnos") String path,
        @DefaultValue("2s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout,
        @DefaultValue("3") long maxRetries,
        @DefaultValue("200ms") Duration delay,
        @DefaultValue("2.0") double multiplier,
        @DefaultValue("2s") Duration maxDelay,
        @DefaultValue("50ms") Duration jitter,
        @DefaultValue("5s") Duration retryAfter) {
}
