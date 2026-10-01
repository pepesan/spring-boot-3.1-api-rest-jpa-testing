package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Aceptación contra el servidor real con {@link RestTemplate} "a pelo" (cliente bloqueante clásico).
 * Equivalente de Spring Boot para tests: {@link AlumnoTestRestTemplateAceptacionTest}.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("Aceptance")
class AlumnoRestTemplateAceptacionTest {

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private String url() {
        return "http://localhost:" + port + "/api/v1/alumnos";
    }

    private Alumno crear(String nombre) {
        ResponseEntity<Alumno> respuesta = restTemplate.postForEntity(
                url() + "/", alumnoDto(nombre), Alumno.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        return respuesta.getBody();
    }

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("RestTemplate");

        assertThat(creado.getId()).isPositive();
        Alumno leido = restTemplate.getForObject(url() + "/{id}", Alumno.class, creado.getId());
        assertThat(leido).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("RestTemplateLis");

        ResponseEntity<List<Alumno>> respuesta = restTemplate.exchange(
                url() + "/", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("RestTemplateDel");

        ResponseEntity<Alumno> borrado = restTemplate.exchange(
                url() + "/{id}", HttpMethod.DELETE, null, Alumno.class, creado.getId());

        assertThat(borrado.getBody()).isEqualTo(creado);
        // RestTemplate lanza excepción ante 4xx/5xx por defecto
        assertThatThrownBy(() -> restTemplate.getForObject(url() + "/{id}", Alumno.class, creado.getId()))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        assertThatThrownBy(() -> restTemplate.postForEntity(url() + "/", alumnoDto("ab"), Alumno.class))
                .isInstanceOf(HttpClientErrorException.BadRequest.class);
    }
}
