package com.inetum.demo.aceptacion;

import com.inetum.demo.controllers.gateway.AlumnoGatewayController;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aceptación del gateway resiliente: el controlador /api/v1/gateway/alumnos llama por RestClient
 * al endpoint inestable de apoyo (/api/v1/inestable/alumnos), que falla las veces que se le pida.
 * Esperas cortas y 2 reintentos (3 llamadas como máximo) para que sea rápido.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "alumnos.gateway.path=/api/v1/inestable/alumnos",
        "alumnos.gateway.max-retries=2",
        "alumnos.gateway.delay=10ms",
        "alumnos.gateway.jitter=0ms",
        "alumnos.gateway.read-timeout=500ms"
})
@AutoConfigureTestRestTemplate
@Tag("Aceptance")
class AlumnoGatewayAceptacionTest {

    private static final String GATEWAY = "/api/v1/gateway/alumnos";
    private static final String INESTABLE = "/api/v1/inestable/alumnos";

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void reset() {
        restTemplate.postForObject(INESTABLE + "/reset", null, String.class);
    }

    private void configurar(int fallos, long lentoMs) {
        restTemplate.postForObject(INESTABLE + "/config?fallos={f}&lentoMs={l}", null, String.class, fallos, lentoMs);
    }

    @SuppressWarnings("unchecked")
    private int llamadasAlRemoto() {
        return ((Map<String, Integer>) restTemplate.getForObject(INESTABLE + "/llamadas", Map.class)).get("llamadas");
    }

    private ResponseEntity<List<Alumno>> listar() {
        return restTemplate.exchange(GATEWAY + "/", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    @Test
    void sinFallosUnaSolaLlamada() {
        ResponseEntity<List<Alumno>> respuesta = listar();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).hasSize(1);
        assertThat(llamadasAlRemoto()).isEqualTo(1);
    }

    @Test
    void seRecuperaDeDosFallosYResponde200ALaTerceraLlamada() {
        configurar(2, 0);

        ResponseEntity<List<Alumno>> respuesta = listar();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().containsHeader(AlumnoGatewayController.HEADER_OBSOLETO)).isFalse();
        assertThat(llamadasAlRemoto()).isEqualTo(3);
    }

    @Test
    void siAgotaLosReintentosResponde503ConRetryAfter() {
        configurar(10, 0);

        ResponseEntity<String> respuesta = restTemplate.getForEntity(GATEWAY + "/{id}", String.class, 1);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(respuesta.getHeaders().getFirst("Retry-After")).isEqualTo("5");
        assertThat(llamadasAlRemoto()).isEqualTo(3);   // 1 intento + 2 reintentos
    }

    @Test
    void siFallaTrasUnListadoBuenoDevuelveElUltimoMarcadoComoObsoleto() {
        assertThat(listar().getStatusCode()).isEqualTo(HttpStatus.OK);   // llena el fallback
        configurar(10, 0);

        ResponseEntity<List<Alumno>> respuesta = listar();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getFirst(AlumnoGatewayController.HEADER_OBSOLETO)).isEqualTo("true");
        assertThat(respuesta.getBody()).hasSize(1);
    }

    @Test
    void unTimeoutSeReintentaYAcabaEn503() {
        configurar(0, 1500);   // el remoto tarda más que read-timeout (500 ms)

        ResponseEntity<String> respuesta = restTemplate.getForEntity(GATEWAY + "/{id}", String.class, 1);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(llamadasAlRemoto()).isEqualTo(3);
    }

    @Test
    void unNotFoundNoSeReintenta() {
        ResponseEntity<String> respuesta = restTemplate.getForEntity(GATEWAY + "/{id}", String.class, 404);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(llamadasAlRemoto()).isEqualTo(1);
    }

    @Test
    void elPostNoSeReintentaAunqueFalle() {
        configurar(10, 0);
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Marta");
        dto.setApellidos("Perez");
        dto.setEdad(30);

        ResponseEntity<String> respuesta = restTemplate.postForEntity(GATEWAY + "/", dto, String.class);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(llamadasAlRemoto()).isEqualTo(1);
    }
}
