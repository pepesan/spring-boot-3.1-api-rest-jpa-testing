package com.inetum.demo.aceptacion;

import com.inetum.demo.dtos.Dato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Tag("Aceptance")
public class HttpRequestTest {

    @Value(value="${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String apiURL;

    @BeforeEach
    public void setUp() {
        apiURL = "http://localhost:" + port + "/";
        this.restTemplate.getForObject(apiURL+"api/v1/dato/clear",
                String.class);
    }

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject(apiURL,
                String.class)).contains("Hola Mundo");
    }

    @Test
    public void testEnviarDatosAlApi() {
        // Crear un objeto MyObject con la cadena que deseas enviar
        Dato dato = new Dato();
        dato.setCadena("Ejemplo de cadena");

        // Configurar las cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Crear la entidad HTTP con el objeto y las cabeceras
        HttpEntity<Dato> requestEntity = new HttpEntity<>(dato, headers);

        // Crear un objeto RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        this.apiURL+="api/v1/dato/";        // Realizar la solicitud POST al API REST
        ResponseEntity<Dato> responseEntity = restTemplate.exchange(
                this.apiURL,
                HttpMethod.POST,
                requestEntity,
                Dato.class
        );

        // Verificar que la respuesta del servidor sea exitosa (código 2xx)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Puedes verificar más cosas según tus necesidades, por ejemplo, el contenido de la respuesta
        Dato responseBody = responseEntity.getBody();
        assertEquals("Ejemplo de cadena", responseBody.getCadena());
        assertEquals(1, responseBody.getId());
    }

    @Test
    public void testEnviarDatosAlApiWebFlux() {
        // Crear un objeto MyObject con la cadena que deseas enviar
        Dato dato = new Dato();
        dato.setCadena("Ejemplo de cadena");

        // Configurar las cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Crear la entidad HTTP con el objeto y las cabeceras
        HttpEntity<Dato> requestEntity = new HttpEntity<>(dato, headers);

        // Crear un objeto WebClient
        WebClient webClient = WebClient.create(this.apiURL);

        // Realizar la solicitud POST al API REST usando WebClient
        ResponseEntity<Dato> responseEntity = webClient.post()
                .uri("/api/v1/dato/") // Puedes cambiar la URI según tu necesidad
                .body(BodyInserters.fromValue(dato))
                .retrieve()
                .toEntity(Dato.class)
                .block(); // Espera la respuesta síncronamente

        // Verificar que la respuesta del servidor sea exitosa (código 2xx)
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Puedes verificar más cosas según tus necesidades, por ejemplo, el contenido de la respuesta
        Dato responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals("Ejemplo de cadena", responseBody.getCadena());
        assertEquals(1, responseBody.getId());
    }

}
