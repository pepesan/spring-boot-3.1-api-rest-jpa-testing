package com.inetum.demo.repositories.clientes;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * Misma consulta que {@link AlumnoRestTemplateApiRepository} con WebClient. La app es MVC
 * (bloqueante), así que se hace {@code block()} al final; en una app reactiva se devolverían
 * directamente los Mono/Flux.
 */
@Repository("alumnoWebClientApiRepository")
public class AlumnoWebClientApiRepository implements AlumnoApiRepository {

    private final WebClient webClient;
    private final AlumnoApiBaseUrl baseUrl;

    public AlumnoWebClientApiRepository(WebClient alumnosWebClient, AlumnoApiBaseUrl baseUrl) {
        this.webClient = alumnosWebClient;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Alumno> findAll() {
        return webClient.get()
                .uri(baseUrl.alumnos() + "/")
                .retrieve()
                .bodyToFlux(Alumno.class)
                .collectList()
                .block();
    }

    @Override
    public Optional<Alumno> findById(Long id) {
        return Optional.ofNullable(webClient.get()
                .uri(baseUrl.alumnos() + "/{id}", id)
                .retrieve()
                .bodyToMono(Alumno.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                .block());
    }

    @Override
    public Alumno create(AlumnoDTO alumno) {
        return webClient.post()
                .uri(baseUrl.alumnos() + "/")
                .bodyValue(alumno)
                .retrieve()
                .bodyToMono(Alumno.class)
                .block();
    }

    @Override
    public Optional<Alumno> delete(Long id) {
        return Optional.ofNullable(webClient.delete()
                .uri(baseUrl.alumnos() + "/{id}", id)
                .retrieve()
                .bodyToMono(Alumno.class)
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                .block());
    }
}
