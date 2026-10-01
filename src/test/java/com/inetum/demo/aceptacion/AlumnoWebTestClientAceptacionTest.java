package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aceptación con {@link WebTestClient}: el equivalente de Spring Boot de WebClient para tests.
 * Apunta al servidor de test y trae aserciones fluidas ({@code expectStatus}, {@code expectBody}...).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Tag("Aceptance")
class AlumnoWebTestClientAceptacionTest {

    private static final String API = "/api/v1/alumnos";

    @Autowired
    private WebTestClient webTestClient;

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    private Alumno crear(String nombre) {
        Alumno creado = webTestClient.post().uri(API + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(alumnoDto(nombre))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class)
                .returnResult().getResponseBody();
        assertThat(creado).isNotNull();
        return creado;
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("WebTestClient");

        webTestClient.get().uri(API + "/{id}", creado.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("WebTestClientLis");

        webTestClient.get().uri(API + "/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Alumno.class).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("WebTestClientDel");

        webTestClient.delete().uri(API + "/{id}", creado.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class).isEqualTo(creado);

        webTestClient.get().uri(API + "/{id}", creado.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        webTestClient.post().uri(API + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(alumnoDto("ab"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
