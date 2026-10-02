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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aceptación del controlador cliente de RestTemplate (/api/v1/clientes/resttemplate/alumnos): cada petición
 * dispara una segunda llamada HTTP del propio servidor hacia /api/v1/alumnos. Se comprueba
 * que lo que se crea/borra a través del cliente es lo que ve el API real.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Tag("Aceptance")
class AlumnoRestTemplateClienteControllerAceptacionTest {

    private static final String CLIENTE = "/api/v1/clientes/resttemplate/alumnos";
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

    private Alumno crearPorCliente(String nombre) {
        ResponseEntity<Alumno> respuesta = restTemplate.postForEntity(
                CLIENTE + "/", alumnoDto(nombre), Alumno.class);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        return respuesta.getBody();
    }

    @Test
    void crearPorClienteLoDejaDisponibleEnElApiReal() {
        Alumno creado = crearPorCliente("ClienteRestTemplate");

        assertThat(creado.getId()).isPositive();
        assertThat(restTemplate.getForObject(API + "/{id}", Alumno.class, creado.getId())).isEqualTo(creado);
    }

    @Test
    void consultarPorIdYListarPorCliente() {
        Alumno creado = crearPorCliente("CliRestTemplateLis");

        assertThat(restTemplate.getForObject(CLIENTE + "/{id}", Alumno.class, creado.getId()))
                .isEqualTo(creado);
        ResponseEntity<List<Alumno>> lista = restTemplate.exchange(
                CLIENTE + "/", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertThat(lista.getBody()).contains(creado);
    }

    @Test
    void borrarPorClienteYLuegoNotFound() {
        Alumno creado = crearPorCliente("CliRestTemplateDel");

        ResponseEntity<Alumno> borrado = restTemplate.exchange(
                CLIENTE + "/{id}", HttpMethod.DELETE, null, Alumno.class, creado.getId());

        assertThat(borrado.getBody()).isEqualTo(creado);
        assertThat(restTemplate.getForEntity(API + "/{id}", String.class, creado.getId()).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(restTemplate.getForEntity(CLIENTE + "/{id}", String.class, creado.getId()).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void putPorClienteReemplazaElAlumnoEnElApiReal() {
        Alumno creado = crearPorCliente("CliRTPut");
        AlumnoDTO nuevo = alumnoDto("CliRTPut2");
        nuevo.setEdad(41);

        ResponseEntity<Alumno> respuesta = restTemplate.exchange(
                CLIENTE + "/{id}", HttpMethod.PUT, new HttpEntity<>(nuevo), Alumno.class, creado.getId());

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getId()).isEqualTo(creado.getId());
        assertThat(respuesta.getBody().getEdad()).isEqualTo(41);
        assertThat(restTemplate.getForObject(API + "/{id}", Alumno.class, creado.getId()).getNombre())
                .isEqualTo("CliRTPut2");
    }

    @Test
    void putPorClienteDeUnIdInexistenteDaNotFoundYInvalidoBadRequest() {
        Alumno creado = crearPorCliente("CliRTPut404");

        assertThat(restTemplate.exchange(CLIENTE + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(alumnoDto("Nuevo")), String.class, creado.getId() + 1000).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(restTemplate.exchange(CLIENTE + "/{id}", HttpMethod.PUT,
                new HttpEntity<>(alumnoDto("ab")), String.class, creado.getId()).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void patchPorClienteModificaSoloLosCamposEnviados() {
        Alumno creado = crearPorCliente("CliRTPatch");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json");

        ResponseEntity<Alumno> respuesta = restTemplate.exchange(CLIENTE + "/{id}", HttpMethod.PATCH,
                new HttpEntity<>("{\"nombre\":\"CliRTPatch2\"}", headers), Alumno.class, creado.getId());

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getNombre()).isEqualTo("CliRTPatch2");
        Alumno enApi = restTemplate.getForObject(API + "/{id}", Alumno.class, creado.getId());
        assertThat(enApi.getNombre()).isEqualTo("CliRTPatch2");
        assertThat(enApi.getApellidos()).isEqualTo(creado.getApellidos());
        assertThat(enApi.getEdad()).isEqualTo(creado.getEdad());
    }

    @Test
    void patchPorClienteDeUnIdInexistenteDaNotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json");

        assertThat(restTemplate.exchange(CLIENTE + "/{id}", HttpMethod.PATCH,
                new HttpEntity<>("{\"nombre\":\"Nadie\"}", headers), String.class, 999999).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
