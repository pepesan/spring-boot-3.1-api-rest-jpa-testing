package com.inetum.demo.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class MiRestStepDefinitions {

    private static final String ROOT_PATH = "/";

    @Autowired
    private WebTestClient webTestClient;

    private HttpStatusCode ultimoStatus;
    private String ultimoCuerpo;

    @Cuando("accedo a la raíz de la API")
    public void accedoALaRaizDeLaApi() {
        WebTestClient.ResponseSpec response = webTestClient.get().uri(ROOT_PATH)
                .exchange();
        ultimoStatus = response.returnResult(String.class).getStatus();
        ultimoCuerpo = response.expectBody(String.class).returnResult().getResponseBody();
    }

    @Cuando("envío un POST a la raíz de la API")
    public void envioUnPostALaRaizDeLaApi() {
        WebTestClient.ResponseSpec response = webTestClient.post().uri(ROOT_PATH)
                .exchange();
        ultimoStatus = response.returnResult(String.class).getStatus();
        ultimoCuerpo = null;
    }

    @Entonces("la respuesta de la raíz tiene código {int}")
    public void laRespuestaDeLaRaizTieneCodigo(int codigo) {
        assertThat(ultimoStatus.value()).isEqualTo(codigo);
    }

    @Entonces("el cuerpo de la respuesta es {string}")
    public void elCuerpoDeLaRespuestaEs(String cuerpo) {
        assertThat(ultimoCuerpo).isEqualTo(cuerpo);
    }

    // --- Equivalentes en inglés (features/mi_rest_en.feature), misma lógica compartida ---

    @When("I access the API root")
    public void iAccessTheApiRoot() {
        accedoALaRaizDeLaApi();
    }

    @When("I send a POST to the API root")
    public void iSendAPostToTheApiRoot() {
        envioUnPostALaRaizDeLaApi();
    }

    @Then("the root response has status code {int}")
    public void theRootResponseHasStatusCode(int codigo) {
        laRespuestaDeLaRaizTieneCodigo(codigo);
    }

    @Then("the response body is {string}")
    public void theResponseBodyIs(String cuerpo) {
        elCuerpoDeLaRespuestaEs(cuerpo);
    }
}
