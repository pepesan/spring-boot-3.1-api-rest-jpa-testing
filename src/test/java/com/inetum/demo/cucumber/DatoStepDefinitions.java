package com.inetum.demo.cucumber;

import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class DatoStepDefinitions {

    private static final String BASE_PATH = "/api/v1/dato";

    @Autowired
    private WebTestClient webTestClient;

    private HttpStatusCode ultimoStatus;
    private Dato ultimoDato;

    @Dado("que no hay ningún dato guardado")
    public void queNoHayNingunDatoGuardado() {
        webTestClient.get().uri(BASE_PATH + "/clear")
                .exchange()
                .expectStatus().isOk();
    }

    @Cuando("creo un dato con la cadena {string}")
    public void creoUnDatoConLaCadena(String cadena) {
        WebTestClient.ResponseSpec response = webTestClient.post().uri(BASE_PATH + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new DatoDTO(cadena))
                .exchange();
        ultimoStatus = response.returnResult(Dato.class).getStatus();
        ultimoDato = response.expectBody(Dato.class).returnResult().getResponseBody();
    }

    @Cuando("consulto el dato por su id")
    public void consultoElDatoPorSuId() {
        consultarDatoPorId(ultimoDato.getId());
    }

    @Cuando("consulto el dato con id {int}")
    public void consultoElDatoConId(long id) {
        consultarDatoPorId(id);
    }

    private void consultarDatoPorId(long id) {
        WebTestClient.ResponseSpec response = webTestClient.get().uri(BASE_PATH + "/" + id)
                .exchange();
        ultimoStatus = response.returnResult(Dato.class).getStatus();
        ultimoDato = response.expectBody(Dato.class).returnResult().getResponseBody();
    }

    @Entonces("la respuesta tiene código {int}")
    public void laRespuestaTieneCodigo(int codigo) {
        assertThat(ultimoStatus.value()).isEqualTo(codigo);
    }

    @Entonces("el dato creado tiene la cadena {string}")
    public void elDatoCreadoTieneLaCadena(String cadena) {
        assertThat(ultimoDato.getCadena()).isEqualTo(cadena);
    }

    @Entonces("el dato consultado tiene la cadena {string}")
    public void elDatoConsultadoTieneLaCadena(String cadena) {
        assertThat(ultimoDato.getCadena()).isEqualTo(cadena);
    }

    // --- Equivalentes en inglés (features/dato_en.feature), misma lógica compartida ---

    @Given("there is no dato stored")
    public void thereIsNoDatoStored() {
        queNoHayNingunDatoGuardado();
    }

    @When("I create a dato with the string {string}")
    public void iCreateADatoWithTheString(String cadena) {
        creoUnDatoConLaCadena(cadena);
    }

    @When("I fetch the dato by its id")
    public void iFetchTheDatoByItsId() {
        consultarDatoPorId(ultimoDato.getId());
    }

    @When("I fetch the dato with id {int}")
    public void iFetchTheDatoWithId(long id) {
        consultarDatoPorId(id);
    }

    @Then("the response has status code {int}")
    public void theResponseHasStatusCode(int codigo) {
        laRespuestaTieneCodigo(codigo);
    }

    @Then("the created dato has the string {string}")
    public void theCreatedDatoHasTheString(String cadena) {
        assertThat(ultimoDato.getCadena()).isEqualTo(cadena);
    }

    @Then("the fetched dato has the string {string}")
    public void theFetchedDatoHasTheString(String cadena) {
        assertThat(ultimoDato.getCadena()).isEqualTo(cadena);
    }
}
