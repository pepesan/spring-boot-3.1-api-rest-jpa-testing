package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Aceptación contra el servidor real con {@link WebClient} (cliente reactivo; se usa {@code block()}
 * para esperar la respuesta de forma síncrona). Equivalente de Spring Boot para tests:
 * {@link AlumnoWebTestClientAceptacionTest}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("Aceptance")
class AlumnoWebClientAceptacionTest {

    @Value("${local.server.port}")
    private int port;

    private WebClient webClient() {
        return WebClient.create("http://localhost:" + port + "/api/v1/alumnos");
    }

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    private Alumno crear(String nombre) {
        return webClient().post().uri("/")
                .bodyValue(alumnoDto(nombre))
                .retrieve()
                .bodyToMono(Alumno.class)
                .block();
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("WebClient");

        assertThat(creado.getId()).isPositive();
        Alumno leido = webClient().get().uri("/{id}", creado.getId())
                .retrieve()
                .bodyToMono(Alumno.class)
                .block();
        assertThat(leido).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("WebClientLis");

        List<Alumno> alumnos = webClient().get().uri("/")
                .retrieve()
                .bodyToFlux(Alumno.class)
                .collectList()
                .block();

        assertThat(alumnos).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("WebClientDel");

        Alumno borrado = webClient().delete().uri("/{id}", creado.getId())
                .retrieve()
                .bodyToMono(Alumno.class)
                .block();

        assertThat(borrado).isEqualTo(creado);
        // retrieve() convierte 4xx/5xx en WebClientResponseException al leer el cuerpo
        assertThatThrownBy(() -> webClient().get().uri("/{id}", creado.getId())
                .retrieve().bodyToMono(Alumno.class).block())
                .isInstanceOf(WebClientResponseException.NotFound.class);
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        // exchangeToMono permite inspeccionar el status sin que se lance excepción
        HttpStatus status = webClient().post().uri("/")
                .bodyValue(alumnoDto("ab"))
                .exchangeToMono(r -> r.releaseBody().thenReturn(HttpStatus.valueOf(r.statusCode().value())))
                .block();

        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
