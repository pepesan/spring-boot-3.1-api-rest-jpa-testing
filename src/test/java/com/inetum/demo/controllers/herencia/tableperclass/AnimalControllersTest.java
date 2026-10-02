package com.inetum.demo.controllers.herencia.tableperclass;

import com.inetum.demo.repositories.herencia.tableperclass.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Herencia TABLE_PER_CLASS: Perro y Gato en tablas independientes, con CRUD por tipo y vista polimórfica. */
@SpringBootTest
@AutoConfigureMockMvc
class AnimalControllersTest {

    private static final String PERRO = "{\"nombre\":\"Rex\",\"edad\":4,\"raza\":\"Labrador\",\"adiestrado\":true}";
    private static final String GATO = "{\"nombre\":\"Misi\",\"edad\":2,\"colorPelo\":\"negro\",\"indoor\":true}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private AnimalRepository animalRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiar() {
        animalRepository.deleteAll();
    }

    private long crear(String ruta, String json) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/" + ruta + "/")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void cadaClaseTieneSuTablaConLasColumnasComunesRepetidas() throws Exception {
        long idPerro = crear("perros", PERRO);
        long idGato = crear("gatos", GATO);

        assertThat(jdbcTemplate.queryForObject(
                "SELECT nombre FROM perros WHERE id = ?", String.class, idPerro)).isEqualTo("Rex");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT raza FROM perros WHERE id = ?", String.class, idPerro)).isEqualTo("Labrador");
        assertThat(jdbcTemplate.queryForObject(
                "SELECT nombre FROM gatos WHERE id = ?", String.class, idGato)).isEqualTo("Misi");
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM perros", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gatos", Integer.class)).isEqualTo(1);
        assertThat(idPerro).isNotEqualTo(idGato);
    }

    @Test
    void animalesListaTodosLosTiposConSuCampoTipo() throws Exception {
        crear("perros", PERRO);
        crear("gatos", GATO);

        mockMvc.perform(get("/api/v1/animales/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.tipo == 'PERRO')].raza").value("Labrador"))
                .andExpect(jsonPath("$[?(@.tipo == 'GATO')].colorPelo").value("negro"));
    }

    @Test
    void animalesShowYDeleteFuncionanConCualquierTipo() throws Exception {
        long id = crear("gatos", GATO);

        mockMvc.perform(get("/api/v1/animales/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("GATO"));
        mockMvc.perform(get("/api/v1/animales/" + (id + 1000))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/animales/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(delete("/api/v1/animales/" + id)).andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM gatos", Integer.class)).isZero();
    }

    @Test
    void cadaControladorSoloListaSuTipoYUnIdAjenoEsNotFound() throws Exception {
        long idPerro = crear("perros", PERRO);
        crear("gatos", GATO);

        mockMvc.perform(get("/api/v1/perros/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/gatos/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/gatos/" + idPerro)).andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/gatos/" + idPerro)
                        .contentType(MediaType.APPLICATION_JSON).content(GATO))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/perros/" + idPerro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rex"));
    }

    @Test
    void putReemplazaLosCamposYMantieneElIdEnCadaTipo() throws Exception {
        long idPerro = crear("perros", PERRO);
        long idGato = crear("gatos", GATO);

        mockMvc.perform(put("/api/v1/perros/" + idPerro).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Rex\",\"edad\":5,\"raza\":\"Pastor\",\"adiestrado\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idPerro))
                .andExpect(jsonPath("$.tipo").value("PERRO"))
                .andExpect(jsonPath("$.raza").value("Pastor"));
        mockMvc.perform(put("/api/v1/gatos/" + idGato).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Misi\",\"edad\":3,\"colorPelo\":\"blanco\",\"indoor\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.colorPelo").value("blanco"));
        mockMvc.perform(put("/api/v1/perros/9999").contentType(MediaType.APPLICATION_JSON).content(PERRO))
                .andExpect(status().isNotFound());
        assertThat(animalRepository.count()).isEqualTo(2);
    }

    @Test
    void datosInvalidosDevuelven400() throws Exception {
        mockMvc.perform(post("/api/v1/perros/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"edad\":-1,\"raza\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/gatos/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Misi\",\"edad\":2,\"colorPelo\":\"\"}"))
                .andExpect(status().isBadRequest());
        assertThat(animalRepository.count()).isZero();
    }
}
