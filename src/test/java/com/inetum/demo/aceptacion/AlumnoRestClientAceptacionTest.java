package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Aceptación contra el servidor real con {@link RestClient}: el cliente HTTP bloqueante moderno
 * de Spring (API fluida como WebClient, sin Reactor). Sustituye a RestTemplate en código nuevo.
 * Equivalente de Spring para tests: {@link AlumnoRestTestClientAceptacionTest}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("Aceptance")
class AlumnoRestClientAceptacionTest {

    @Value("${local.server.port}")
    private int port;

    private RestClient restClient() {
        return RestClient.create("http://localhost:" + port + "/api/v1/alumnos");
    }

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    private Alumno crear(String nombre) {
        return restClient().post().uri("/")
                .body(alumnoDto(nombre))
                .retrieve()
                .body(Alumno.class);
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("RestClient");

        assertThat(creado.getId()).isPositive();
        Alumno leido = restClient().get().uri("/{id}", creado.getId())
                .retrieve()
                .body(Alumno.class);
        assertThat(leido).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("RestClientLis");

        List<Alumno> alumnos = restClient().get().uri("/")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(alumnos).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("RestClientDel");

        Alumno borrado = restClient().delete().uri("/{id}", creado.getId())
                .retrieve()
                .body(Alumno.class);

        assertThat(borrado).isEqualTo(creado);
        // retrieve() lanza excepción ante 4xx/5xx, como RestTemplate
        assertThatThrownBy(() -> restClient().get().uri("/{id}", creado.getId())
                .retrieve().body(Alumno.class))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        // toBodilessEntity + onStatus vacío evita la excepción y permite inspeccionar el status
        HttpStatus status = (HttpStatus) restClient().post().uri("/")
                .body(alumnoDto("ab"))
                .retrieve()
                .onStatus(s -> s.is4xxClientError(), (request, response) -> { })
                .toBodilessEntity()
                .getStatusCode();

        assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
