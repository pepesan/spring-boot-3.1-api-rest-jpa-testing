package com.inetum.demo.gateways;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.core.retry.Retryable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;

/**
 * Gateway con {@link RestClient} y reintentos de Spring Framework 7 ({@link RetryTemplate}).
 * <p>
 * Solo se reintentan las operaciones idempotentes (GET, PUT y DELETE). {@link #create} NO se reintenta:
 * si el servidor guardó el alumno pero la respuesta se perdió, reintentar lo duplicaría. Tampoco
 * {@link #patch}: según HTTP, PATCH no es idempotente en general (un parche podría ser relativo).
 */
@Slf4j
@Component
public class AlumnoRestClientGateway implements AlumnoGateway {

    private final RestClient restClient;
    private final RetryTemplate retryTemplate;
    private final AlumnoApiBaseUrl baseUrl;
    private final AlumnoGatewayProperties props;

    public AlumnoRestClientGateway(RestClient alumnoGatewayRestClient, RetryTemplate alumnoGatewayRetryTemplate,
                                   AlumnoApiBaseUrl baseUrl, AlumnoGatewayProperties props) {
        this.restClient = alumnoGatewayRestClient;
        this.retryTemplate = alumnoGatewayRetryTemplate;
        this.baseUrl = baseUrl;
        this.props = props;
    }

    private String url() {
        return baseUrl.get() + props.path();
    }

    @Override
    public List<Alumno> findAll() {
        return conReintentos("findAll", () -> restClient.get()
                .uri(url() + "/")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Alumno>>() {}));
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return conReintentos("findById", () -> {
            try {
                return Optional.ofNullable(restClient.get()
                        .uri(url() + "/{id}", id)
                        .retrieve()
                        .body(Alumno.class));
            } catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();   // "no existe" es una respuesta válida, no un fallo
            }
        });
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        try {
            return restClient.post()
                    .uri(url() + "/")
                    .body(alumno)
                    .retrieve()
                    .body(Alumno.class);
        } catch (RuntimeException e) {
            throw traducir("create", e);
        }
    }

    @Override
    public Optional<Alumno> update(Long id, AlumnoDTO alumno) {
        return conReintentos("update", () -> {
            try {
                return Optional.ofNullable(restClient.put()
                        .uri(url() + "/{id}", id)
                        .body(alumno)
                        .retrieve()
                        .body(Alumno.class));
            } catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<Alumno> patch(Long id, JsonNode patch) {
        try {
            return Optional.ofNullable(restClient.patch()
                    .uri(url() + "/{id}", id)
                    .contentType(MediaType.valueOf("application/merge-patch+json"))
                    .body(patch)
                    .retrieve()
                    .body(Alumno.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RuntimeException e) {
            throw traducir("patch", e);
        }
    }

    @Override
    public Optional<Alumno> delete(Long id) {
        return conReintentos("delete", () -> {
            try {
                return Optional.ofNullable(restClient.delete()
                        .uri(url() + "/{id}", id)
                        .retrieve()
                        .body(Alumno.class));
            } catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();
            }
        });
    }

    private <R> R conReintentos(String operacion, Retryable<R> accion) {
        try {
            return retryTemplate.execute(accion);
        } catch (RetryException e) {
            log.warn("GATEWAY: {} fallida tras {} reintento(s): {}", operacion, e.getRetryCount(),
                    e.getLastException().toString());
            throw traducir(operacion, e.getLastException());
        }
    }

    private AlumnoGatewayException traducir(String operacion, Throwable causa) {
        boolean rechazadoPorElRemoto = causa instanceof HttpClientErrorException;
        return new AlumnoGatewayException(
                "El servicio remoto de alumnos falló en " + operacion + ": " + causa.getMessage(),
                causa, !rechazadoPorElRemoto);
    }
}
