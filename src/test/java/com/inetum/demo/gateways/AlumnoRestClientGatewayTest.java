package com.inetum.demo.gateways;

import com.inetum.demo.clientes.AlumnoApiBaseUrl;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Reintentos y traducción de errores del gateway, sin red: MockRestServiceServer cuenta las llamadas. */
class AlumnoRestClientGatewayTest {

    private static final String URL = "http://remoto/api/v1/alumnos";
    private static final String ALUMNO_JSON = "{\"id\":1,\"nombre\":\"Marta\",\"apellidos\":\"Perez\",\"edad\":30}";

    private MockRestServiceServer server;
    private AlumnoRestClientGateway gateway;

    @BeforeEach
    void setUp() {
        // 3 reintentos (hasta 4 llamadas) con esperas mínimas para que el test sea rápido
        AlumnoGatewayProperties props = new AlumnoGatewayProperties("/api/v1/alumnos",
                Duration.ofSeconds(1), Duration.ofSeconds(1), 3, Duration.ofMillis(1), 1.0,
                Duration.ofMillis(1), Duration.ofMillis(1), Duration.ofSeconds(5));
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RetryTemplate retryTemplate = new AlumnoGatewayConfig().alumnoGatewayRetryTemplate(props);
        AlumnoApiBaseUrl baseUrl = new AlumnoApiBaseUrl(
                new MockEnvironment().withProperty("alumnos.api.base-url", "http://remoto"));
        gateway = new AlumnoRestClientGateway(builder.build(), retryTemplate, baseUrl, props);
    }

    @Test
    void reintentaTrasLosFallos500YTerminaBien() {
        server.expect(ExpectedCount.times(2), requestTo(URL + "/")).andRespond(withServerError());
        server.expect(ExpectedCount.once(), requestTo(URL + "/"))
                .andRespond(withSuccess("[" + ALUMNO_JSON + "]", APPLICATION_JSON));

        List<Alumno> alumnos = gateway.findAll();

        assertThat(alumnos).extracting(Alumno::getNombre).containsExactly("Marta");
        server.verify();   // exactamente 3 llamadas: 2 fallidas + 1 buena
    }

    @Test
    void agotaLosReintentosYLanzaExcepcionTransitoria() {
        server.expect(ExpectedCount.times(4), requestTo(URL + "/")).andRespond(withServerError());

        assertThatThrownBy(() -> gateway.findAll())
                .isInstanceOfSatisfying(AlumnoGatewayException.class, e -> assertThat(e.isTransitorio()).isTrue());
        server.verify();   // 1 intento + 3 reintentos
    }

    @Test
    void reintentaTambienLosFalloDeRed() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/"))
                .andRespond(request -> { throw new IOException("connection reset"); });
        server.expect(ExpectedCount.once(), requestTo(URL + "/"))
                .andRespond(withSuccess("[]", APPLICATION_JSON));

        assertThat(gateway.findAll()).isEmpty();
        server.verify();
    }

    @Test
    void unNotFoundNoSeReintentaYDevuelveVacio() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/404")).andRespond(withResourceNotFound());

        assertThat(gateway.findById(404L)).isEmpty();
        server.verify();
    }

    @Test
    void unBadRequestNoSeReintentaYEsNoTransitorio() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/")).andExpect(method(POST))
                .andRespond(withBadRequest());

        assertThatThrownBy(() -> gateway.create(new AlumnoDTO()))
                .isInstanceOfSatisfying(AlumnoGatewayException.class, e -> assertThat(e.isTransitorio()).isFalse());
        server.verify();
    }

    @Test
    void elPostNoSeReintentaAunqueDeError500() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/")).andExpect(method(POST))
                .andRespond(withServerError());

        assertThatThrownBy(() -> gateway.create(new AlumnoDTO()))
                .isInstanceOfSatisfying(AlumnoGatewayException.class, e -> assertThat(e.isTransitorio()).isTrue());
        server.verify();   // una única llamada: reintentar duplicaría el alumno si el servidor sí lo guardó
    }

    @Test
    void creaYLeePorId() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/")).andExpect(method(POST))
                .andRespond(withSuccess(ALUMNO_JSON, APPLICATION_JSON));
        server.expect(ExpectedCount.once(), requestTo(URL + "/1")).andExpect(method(GET))
                .andRespond(withSuccess(ALUMNO_JSON, APPLICATION_JSON));

        Alumno creado = gateway.create(new AlumnoDTO());
        Optional<Alumno> leido = gateway.findById(1L);

        assertThat(leido).contains(creado);
        server.verify();
    }

    @Test
    void borraYReintentaElDeleteSiFalla() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/1")).andExpect(method(DELETE))
                .andRespond(withServerError());
        server.expect(ExpectedCount.once(), requestTo(URL + "/1")).andExpect(method(DELETE))
                .andRespond(withSuccess(ALUMNO_JSON, APPLICATION_JSON));

        assertThat(gateway.delete(1L)).map(Alumno::getNombre).contains("Marta");
        server.verify();
    }

    @Test
    void borrarUnInexistenteDevuelveVacio() {
        server.expect(ExpectedCount.once(), requestTo(URL + "/9")).andRespond(withResourceNotFound());

        assertThat(gateway.delete(9L)).isEmpty();
        server.verify();
    }
}
