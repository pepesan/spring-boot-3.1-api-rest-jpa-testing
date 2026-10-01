package com.inetum.demo.controllers.onetoone;

import com.inetum.demo.repositories.onetoone.PhoneDetailsRepository;
import com.inetum.demo.repositories.onetoone.PhoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** CRUD completo de Phone (+ PhoneDetails anidado), búsqueda y PATCH. */
@SpringBootTest
@AutoConfigureMockMvc
class OneToOnePhoneControllerTest {

    private static final String BASE = "/api/v1/onetoone";
    private static final String MERGE_PATCH = "application/merge-patch+json";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private PhoneDetailsRepository phoneDetailsRepository;

    @BeforeEach
    void limpiar() {
        phoneRepository.deleteAll();
        phoneDetailsRepository.deleteAll();
    }

    private static String phoneJson(String number, String provider, String technology) {
        String details = provider == null ? "" :
                ",\"details\":{\"provider\":\"" + provider + "\",\"technology\":\"" + technology + "\"}";
        return "{\"number\":\"" + number + "\"" + details + "}";
    }

    private long crear(String number, String provider, String technology) throws Exception {
        MvcResult r = mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson(number, provider, technology)))
                .andExpect(status().isCreated())
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    // ---------- seed y listado ----------

    @Test
    void indexListsWithoutCreatingAnything() throws Exception {
        mockMvc.perform(get(BASE + "/")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get(BASE + "/")).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void seedCreatesPhoneWithDetailsAndProviderFindsIt() throws Exception {
        mockMvc.perform(post(BASE + "/seed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value("923124578"))
                .andExpect(jsonPath("$[0].details.provider").value("PepePhone"))
                .andExpect(jsonPath("$[0].details.technology").value("5G"));
        mockMvc.perform(get(BASE + "/provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].details.provider").value("PepePhone"));
    }

    // ---------- create / show ----------

    @Test
    void createReturns201WithLocationAndDetails() throws Exception {
        MvcResult r = mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("600111222", "PepePhone", "5G")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.details.id").isNumber())
                .andExpect(jsonPath("$.details.provider").value("PepePhone"))
                .andReturn();

        String location = r.getResponse().getHeader("Location");
        assertThat(location).endsWith(BASE + "/" + mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong());
        mockMvc.perform(get(location)).andExpect(status().isOk()).andExpect(jsonPath("$.number").value("600111222"));
    }

    @Test
    void createWithoutDetailsIsAllowed() throws Exception {
        long id = crear("600111222", null, null);

        mockMvc.perform(get(BASE + "/" + id))
                .andExpect(jsonPath("$.number").value("600111222"))
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    @Test
    void createWithInvalidDataReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("12", "", "5G")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.number").value("el número debe tener 9 dígitos."))
                .andExpect(jsonPath("$.errors['details.provider']").exists());
    }

    @Test
    void showNotFoundReturns404() throws Exception {
        mockMvc.perform(get(BASE + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---------- PUT ----------

    @Test
    void putReplacesNumberAndUpdatesDetailsKeepingTheirId() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");
        MvcResult antes = mockMvc.perform(get(BASE + "/" + id)).andReturn();
        long detailsId = mapper.readTree(antes.getResponse().getContentAsString()).get("details").get("id").asLong();

        mockMvc.perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("700333444", "Movistar", "4G")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("700333444"))
                .andExpect(jsonPath("$.details.id").value(detailsId))
                .andExpect(jsonPath("$.details.provider").value("Movistar"))
                .andExpect(jsonPath("$.details.technology").value("4G"));
        assertThat(phoneDetailsRepository.count()).isEqualTo(1);
    }

    @Test
    void putWithoutDetailsRemovesThem() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("600111222", null, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details").doesNotExist());
        assertThat(phoneDetailsRepository.count()).isZero();
    }

    @Test
    void putCanAddDetailsToAPhoneWithoutThem() throws Exception {
        long id = crear("600111222", null, null);

        mockMvc.perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("600111222", "Orange", "5G")))
                .andExpect(jsonPath("$.details.provider").value("Orange"));
        assertThat(phoneDetailsRepository.count()).isEqualTo(1);
    }

    @Test
    void putNotFoundReturns404() throws Exception {
        mockMvc.perform(put(BASE + "/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(phoneJson("600111222", "Orange", "5G")))
                .andExpect(status().isNotFound());
    }

    // ---------- PATCH ----------

    @Test
    void patchNumberKeepsDetails() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(patch(BASE + "/" + id).contentType(MERGE_PATCH).content("{\"number\":\"611222333\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("611222333"))
                .andExpect(jsonPath("$.details.provider").value("PepePhone"))
                .andExpect(jsonPath("$.details.technology").value("5G"));
    }

    @Test
    void patchPartialDetailsMergesWithExistingOnes() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(patch(BASE + "/" + id).contentType(MERGE_PATCH).content("{\"details\":{\"technology\":\"4G\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("600111222"))
                .andExpect(jsonPath("$.details.provider").value("PepePhone"))
                .andExpect(jsonPath("$.details.technology").value("4G"));
        mockMvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.details.technology").value("4G"));
        assertThat(phoneDetailsRepository.count()).isEqualTo(1);
    }

    @Test
    void patchDetailsNullRemovesThem() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(patch(BASE + "/" + id).contentType(MERGE_PATCH).content("{\"details\":null}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("600111222"))
                .andExpect(jsonPath("$.details").doesNotExist());
        assertThat(phoneDetailsRepository.count()).isZero();
    }

    @Test
    void patchCanCreateDetailsOnAPhoneWithoutThem() throws Exception {
        long id = crear("600111222", null, null);

        mockMvc.perform(patch(BASE + "/" + id).contentType(MERGE_PATCH)
                        .content("{\"details\":{\"provider\":\"Orange\",\"technology\":\"5G\"}}"))
                .andExpect(jsonPath("$.details.provider").value("Orange"));
    }

    @Test
    void patchWithInvalidValueReturns400AndKeepsPhone() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(patch(BASE + "/" + id).contentType(MERGE_PATCH).content("{\"details\":{\"provider\":\"\"}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['details.provider']").exists());
        mockMvc.perform(get(BASE + "/" + id)).andExpect(jsonPath("$.details.provider").value("PepePhone"));
    }

    @Test
    void patchNotFoundReturns404() throws Exception {
        mockMvc.perform(patch(BASE + "/999999").contentType(MERGE_PATCH).content("{\"number\":\"611222333\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchWithPlainJsonContentTypeReturns415() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(patch(BASE + "/" + id).contentType(MediaType.APPLICATION_JSON).content("{\"number\":\"611222333\"}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // ---------- DELETE ----------

    @Test
    void deleteRemovesPhoneAndItsDetails() throws Exception {
        long id = crear("600111222", "PepePhone", "5G");

        mockMvc.perform(delete(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
        assertThat(phoneDetailsRepository.count()).isZero();
    }

    @Test
    void deleteNotFoundReturns404() throws Exception {
        mockMvc.perform(delete(BASE + "/999999")).andExpect(status().isNotFound());
    }

    // ---------- search ----------

    private void sembrarBusqueda() throws Exception {
        crear("600111222", "PepePhone", "5G");
        crear("600999888", "Movistar", "4G");
        crear("711000111", "PepePhone", "4G");
        crear("722000222", null, null);
    }

    @Test
    void searchWithoutFiltersReturnsAllPaginated() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.content", hasSize(4)))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void searchByProviderIgnoresCase() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search").param("provider", "pepephone"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void searchByNumberContains() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search").param("number", "6001"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].number").value("600111222"));
    }

    @Test
    void searchCombinesFiltersWithAnd() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search").param("provider", "PepePhone").param("technology", "4g"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].number").value("711000111"));
    }

    @Test
    void searchPaginates() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search").param("page", "2").param("size", "3"))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void searchWithNoMatchesReturnsEmptyPage() throws Exception {
        sembrarBusqueda();

        mockMvc.perform(get(BASE + "/search").param("provider", "Inexistente"))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
