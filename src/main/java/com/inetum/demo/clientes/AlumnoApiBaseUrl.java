package com.inetum.demo.clientes;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resuelve la URL base del API de alumnos a la que apuntan los clientes HTTP.
 * <p>
 * Prioridad: propiedad {@code alumnos.api.base-url} (para apuntar a otro servidor) y, si no está,
 * este mismo servidor: {@code local.server.port} (puerto real, también con RANDOM_PORT en tests)
 * o, en su defecto, {@code server.port}. Se resuelve en cada llamada porque el puerto real
 * solo se conoce una vez arrancado el servidor web.
 */
@Component
public class AlumnoApiBaseUrl {

    private final Environment environment;

    public AlumnoApiBaseUrl(Environment environment) {
        this.environment = environment;
    }

    public String get() {
        String configurada = environment.getProperty("alumnos.api.base-url");
        if (configurada != null && !configurada.isBlank()) {
            return configurada;
        }
        String puerto = environment.getProperty("local.server.port",
                environment.getProperty("server.port", "8080"));
        return "http://localhost:" + puerto;
    }

    public String alumnos() {
        return get() + "/api/v1/alumnos";
    }
}
