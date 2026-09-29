package com.inetum.demo.cucumber;

import com.inetum.demo.dtos.Dato;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Steps equivalentes a MiApiDatoControllerTest, sobre /api/dato. */
public class MiApiDatoStepDefinitions {

    private static final String BASE_PATH = "/api/dato";

    @Autowired
    private WebTestClient webTestClient;

    private final JsonMapper mapper = new JsonMapper();
    private EntityExchangeResult<String> ultimaRespuesta;

    @Dado("que el listado de datos está vacío")
    public void queElListadoDeDatosEstaVacio() {
        webTestClient.get().uri(BASE_PATH + "/clear")
                .exchange()
                .expectStatus().isOk();
    }

    @Cuando("pido el listado de datos")
    public void pidoElListadoDeDatos() {
        ultimaRespuesta = webTestClient.get().uri(BASE_PATH)
                .exchange()
                .expectBody(String.class).returnResult();
    }

    @Cuando("añado un dato al listado con la cadena {string}")
    public void anadoUnDatoAlListadoConLaCadena(String cadena) {
        ultimaRespuesta = webTestClient.post().uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Dato(0L, cadena))
                .exchange()
                .expectBody(String.class).returnResult();
    }

    @Cuando("pido el dato del listado con id {int}")
    public void pidoElDatoDelListadoConId(long id) {
        ultimaRespuesta = webTestClient.get().uri(BASE_PATH + "/" + id)
                .exchange()
                .expectBody(String.class).returnResult();
    }

    @Cuando("modifico el dato del listado con id {int} con la cadena {string}")
    public void modificoElDatoDelListadoConId(long id, String cadena) {
        ultimaRespuesta = webTestClient.put().uri(BASE_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new Dato(0L, cadena))
                .exchange()
                .expectBody(String.class).returnResult();
    }

    @Cuando("elimino el dato del listado con id {int}")
    public void eliminoElDatoDelListadoConId(long id) {
        ultimaRespuesta = webTestClient.delete().uri(BASE_PATH + "/" + id)
                .exchange()
                .expectBody(String.class).returnResult();
    }

    @Entonces("la respuesta del listado tiene código {int}")
    public void laRespuestaDelListadoTieneCodigo(int codigo) {
        assertThat(ultimaRespuesta.getStatus().value()).isEqualTo(codigo);
    }

    @Entonces("la respuesta del listado tiene código {int} y es JSON")
    public void laRespuestaDelListadoTieneCodigoYEsJson(int codigo) {
        laRespuestaDelListadoTieneCodigo(codigo);
        assertThat(ultimaRespuesta.getResponseHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Entonces("el listado devuelto está vacío")
    public void elListadoDevueltoEstaVacio() {
        List<Dato> lista = mapper.readValue(ultimaRespuesta.getResponseBody(),
                new TypeReference<List<Dato>>() { });
        assertThat(lista).isEmpty();
    }

    @Entonces("el dato del listado devuelto tiene id {int} y cadena {string}")
    public void elDatoDelListadoDevueltoTieneIdYCadena(long id, String cadena) {
        Dato dato = mapper.readValue(ultimaRespuesta.getResponseBody(), Dato.class);
        assertThat(dato).isEqualTo(new Dato(id, cadena));
    }

    @Entonces("el dato del listado devuelto está vacío")
    public void elDatoDelListadoDevueltoEstaVacio() {
        Dato dato = mapper.readValue(ultimaRespuesta.getResponseBody(), Dato.class);
        assertThat(dato).isEqualTo(new Dato());
    }

    // --- Equivalentes en inglés (features/mi_api_dato_en.feature), misma lógica compartida ---

    @Given("the data list is empty")
    public void theDataListIsEmpty() {
        queElListadoDeDatosEstaVacio();
    }

    @When("I request the data list")
    public void iRequestTheDataList() {
        pidoElListadoDeDatos();
    }

    @When("I add a data item to the list with the string {string}")
    public void iAddADataItemToTheList(String cadena) {
        anadoUnDatoAlListadoConLaCadena(cadena);
    }

    @When("I request the list item with id {int}")
    public void iRequestTheListItemWithId(long id) {
        pidoElDatoDelListadoConId(id);
    }

    @When("I update the list item with id {int} with the string {string}")
    public void iUpdateTheListItemWithId(long id, String cadena) {
        modificoElDatoDelListadoConId(id, cadena);
    }

    @When("I delete the list item with id {int}")
    public void iDeleteTheListItemWithId(long id) {
        eliminoElDatoDelListadoConId(id);
    }

    @Then("the list response has status code {int}")
    public void theListResponseHasStatusCode(int codigo) {
        laRespuestaDelListadoTieneCodigo(codigo);
    }

    @Then("the list response has status code {int} and is JSON")
    public void theListResponseHasStatusCodeAndIsJson(int codigo) {
        laRespuestaDelListadoTieneCodigoYEsJson(codigo);
    }

    @Then("the returned list is empty")
    public void theReturnedListIsEmpty() {
        elListadoDevueltoEstaVacio();
    }

    @Then("the returned list item has id {int} and string {string}")
    public void theReturnedListItemHasIdAndString(long id, String cadena) {
        elDatoDelListadoDevueltoTieneIdYCadena(id, cadena);
    }

    @Then("the returned list item is empty")
    public void theReturnedListItemIsEmpty() {
        elDatoDelListadoDevueltoEstaVacio();
    }
}
