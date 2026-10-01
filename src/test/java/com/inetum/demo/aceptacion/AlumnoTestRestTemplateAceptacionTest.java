package com.inetum.demo.aceptacion;

import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aceptación con {@link TestRestTemplate}: el equivalente de Spring Boot de RestTemplate para tests.
 * Ya apunta al puerto del servidor de test (rutas relativas) y NO lanza excepción ante 4xx/5xx:
 * devuelve el ResponseEntity para poder comprobar el status.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Tag("Aceptance")
class AlumnoTestRestTemplateAceptacionTest {

    private static final String API = "/api/v1/alumnos";

    @Autowired
    private TestRestTemplate restTemplate;

    private static AlumnoDTO alumnoDto(String nombre) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos("Perez");
        dto.setEdad(30);
        return dto;
    }

    private Alumno crear(String nombre) {
        ResponseEntity<Alumno> respuesta = restTemplate.postForEntity(API + "/", alumnoDto(nombre), Alumno.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        return respuesta.getBody();
    }

    @Test
    void crearYConsultarPorId() {
        Alumno creado = crear("TestRestTemplate");

        ResponseEntity<Alumno> respuesta = restTemplate.getForEntity(API + "/{id}", Alumno.class, creado.getId());

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isEqualTo(creado);
    }

    @Test
    void listarIncluyeElAlumnoCreado() {
        Alumno creado = crear("TestRestTemplateLis");

        ResponseEntity<List<Alumno>> respuesta = restTemplate.exchange(
                API + "/", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).contains(creado);
    }

    @Test
    void borrarDevuelveElAlumnoYLuegoDaNotFound() {
        Alumno creado = crear("TestRestTemplateDel");

        ResponseEntity<Alumno> borrado = restTemplate.exchange(
                API + "/{id}", HttpMethod.DELETE, null, Alumno.class, creado.getId());

        assertThat(borrado.getBody()).isEqualTo(creado);
        assertThat(restTemplate.getForEntity(API + "/{id}", String.class, creado.getId()).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void alumnoInvalidoDaBadRequest() {
        assertThat(restTemplate.postForEntity(API + "/", alumnoDto("ab"), String.class).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
