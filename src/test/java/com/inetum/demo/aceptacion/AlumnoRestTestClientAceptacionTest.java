package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aceptación con {@link RestTestClient}: el equivalente para tests de RestClient (Spring Framework 7).
 * Misma API fluida de aserciones que WebTestClient, pero sin Reactor.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@Tag("Aceptance")
class AlumnoRestTestClientAceptacionTest {

    private static final String API = "/api/v1/alumnos";

    @Autowired
    private RestTestClient restTestClient;

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    private Alumno crear(String nombre) {
        Alumno creado = restTestClient.post().uri(API + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(alumnoDto(nombre))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class)
                .returnResult().getResponseBody();
        assertThat(creado).isNotNull();
        return creado;
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("RestTestClient");

        restTestClient.get().uri(API + "/{id}", creado.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("RestTestClientLis");

        List<Alumno> alumnos = restTestClient.get().uri(API + "/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Alumno>>() {})
                .returnResult().getResponseBody();

        assertThat(alumnos).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("RestTestClientDel");

        restTestClient.delete().uri(API + "/{id}", creado.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Alumno.class).isEqualTo(creado);

        restTestClient.get().uri(API + "/{id}", creado.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        restTestClient.post().uri(API + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(alumnoDto("ab"))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
