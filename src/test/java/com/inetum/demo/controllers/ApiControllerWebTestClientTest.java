package com.inetum.demo.controllers;

import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Pruebas del mismo API que {@link ApiControllerTest} pero a nivel de cliente reactivo:
 * WebTestClient contra el servidor real levantado en un puerto aleatorio (no MockMvc).
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiControllerWebTestClientTest {

    @Autowired
    private WebTestClient webTestClient;

    private final String basePath = "/api/v1/dato";

    @BeforeEach
    void clearRestData() {
        webTestClient.get().uri(basePath + "/clear")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void listShouldReturnOkResultEmpty() {
        webTestClient.get().uri(basePath + "/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Dato.class).hasSize(0);
    }

    @Test
    void addShouldReturnDato() {
        webTestClient.post().uri(basePath + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DatoDTO("valor"))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Dato.class)
                .isEqualTo(new Dato(1L, "valor"));
    }

    @Test
    void getByIdShouldReturnDato() {
        addShouldReturnDato();

        webTestClient.get().uri(basePath + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Dato.class)
                .isEqualTo(new Dato(1L, "valor"));
    }

    @Test
    void getByIdShouldReturnNotFound() {
        webTestClient.get().uri(basePath + "/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateShouldReturnDato() {
        addShouldReturnDato();

        webTestClient.put().uri(basePath + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DatoDTO("valor1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Dato.class)
                .isEqualTo(new Dato(1L, "valor1"));
    }

    @Test
    void removeByIdShouldReturnDato() {
        addShouldReturnDato();

        webTestClient.delete().uri(basePath + "/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Dato.class)
                .isEqualTo(new Dato(1L, "valor"));

        webTestClient.get().uri(basePath + "/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Dato.class).hasSize(0);
    }

    @Test
    void removeByIdShouldReturnNotFoundWithErrorBody() {
        webTestClient.delete().uri(basePath + "/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Not found with id = 1");
    }
}
