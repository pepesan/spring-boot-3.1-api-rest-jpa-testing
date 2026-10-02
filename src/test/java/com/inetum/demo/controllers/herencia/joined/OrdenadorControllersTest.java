package com.inetum.demo.controllers.herencia.joined;

import com.inetum.demo.repositories.herencia.joined.OrdenadorRepository;
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

/** Herencia JOINED: Portatil y Sobremesa en tablas propias enlazadas a "ordenador", con CRUD por tipo y vista polimórfica. */
@SpringBootTest
@AutoConfigureMockMvc
class OrdenadorControllersTest {

    private static final String PORTATIL = "{\"marca\":\"Lenovo\",\"modelo\":\"T14\",\"peso\":1.5,\"duracionBateria\":12}";
    private static final String SOBREMESA = "{\"marca\":\"Dell\",\"modelo\":\"Optiplex\",\"tipoTorre\":\"Full\",\"tieneMonitor\":true}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private OrdenadorRepository ordenadorRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiar() {
        ordenadorRepository.deleteAll();
    }

    private long crear(String ruta, String json) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/" + ruta + "/")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    // ---------- una tabla por clase, enlazadas por FK ----------

    @Test
    void cadaClaseGuardaSusCamposEnSuPropiaTablaEnlazadaPorFk() throws Exception {
        long id = crear("portatiles", PORTATIL);
        crear("sobremesas", SOBREMESA);

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordenador", Integer.class)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM portatil", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sobremesa", Integer.class)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT duracion_bateria FROM portatil WHERE ord_id = ?", Integer.class, id)).isEqualTo(12);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT marca FROM ordenador WHERE id = ?", String.class, id)).isEqualTo("Lenovo");
    }

    // ---------- vista polimórfica ----------

    @Test
    void ordenadoresListaTodosLosTiposConSuCampoTipo() throws Exception {
        crear("portatiles", PORTATIL);
        crear("sobremesas", SOBREMESA);

        mockMvc.perform(get("/api/v1/ordenadores/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.tipo == 'PORTATIL')].duracionBateria").value(12))
                .andExpect(jsonPath("$[?(@.tipo == 'SOBREMESA')].tipoTorre").value("Full"));
    }

    @Test
    void ordenadoresShowYDeleteFuncionanConCualquierTipo() throws Exception {
        long id = crear("sobremesas", SOBREMESA);

        mockMvc.perform(get("/api/v1/ordenadores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("SOBREMESA"));
        mockMvc.perform(get("/api/v1/ordenadores/" + (id + 1000))).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/ordenadores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get("/api/v1/ordenadores/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/ordenadores/" + id)).andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sobremesa", Integer.class)).isZero();
    }

    // ---------- consultas por subclase ----------

    @Test
    void cadaControladorSoloListaSuTipoYUnIdAjenoEsNotFound() throws Exception {
        long idPortatil = crear("portatiles", PORTATIL);
        crear("sobremesas", SOBREMESA);

        mockMvc.perform(get("/api/v1/portatiles/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/sobremesas/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/sobremesas/" + idPortatil)).andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/sobremesas/" + idPortatil)
                        .contentType(MediaType.APPLICATION_JSON).content(SOBREMESA))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/portatiles/" + idPortatil))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("T14"));
    }

    // ---------- altas y modificaciones por tipo ----------

    @Test
    void createDevuelve201ConLosCamposDeCadaTipo() throws Exception {
        mockMvc.perform(post("/api/v1/portatiles/").contentType(MediaType.APPLICATION_JSON).content(PORTATIL))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.tipo").value("PORTATIL"))
                .andExpect(jsonPath("$.peso").value(1.5));
        mockMvc.perform(post("/api/v1/sobremesas/").contentType(MediaType.APPLICATION_JSON).content(SOBREMESA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("SOBREMESA"))
                .andExpect(jsonPath("$.tieneMonitor").value(true));
    }

    @Test
    void putReemplazaLosCamposYMantieneElIdEnCadaTipo() throws Exception {
        long idPortatil = crear("portatiles", PORTATIL);
        long idSobremesa = crear("sobremesas", SOBREMESA);

        mockMvc.perform(put("/api/v1/portatiles/" + idPortatil).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Dell\",\"modelo\":\"XPS\",\"peso\":1.2,\"duracionBateria\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idPortatil))
                .andExpect(jsonPath("$.marca").value("Dell"))
                .andExpect(jsonPath("$.duracionBateria").value(15));
        mockMvc.perform(put("/api/v1/sobremesas/" + idSobremesa).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"HP\",\"modelo\":\"Elite\",\"tipoTorre\":\"Mini\",\"tieneMonitor\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoTorre").value("Mini"));

        mockMvc.perform(get("/api/v1/portatiles/" + idPortatil)).andExpect(jsonPath("$.modelo").value("XPS"));
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ordenador", Integer.class)).isEqualTo(2);
    }

    @Test
    void putDeUnIdInexistenteDevuelve404EnCadaTipo() throws Exception {
        mockMvc.perform(put("/api/v1/portatiles/9999").contentType(MediaType.APPLICATION_JSON).content(PORTATIL))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/sobremesas/9999").contentType(MediaType.APPLICATION_JSON).content(SOBREMESA))
                .andExpect(status().isNotFound());
    }

    // ---------- validación ----------

    @Test
    void datosInvalidosDevuelven400() throws Exception {
        mockMvc.perform(post("/api/v1/portatiles/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"\",\"modelo\":\"T14\",\"peso\":-1,\"duracionBateria\":-5}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/sobremesas/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Dell\",\"modelo\":\"Optiplex\",\"tipoTorre\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/v1/portatiles/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"\"}"))
                .andExpect(status().isBadRequest());
        assertThat(ordenadorRepository.count()).isZero();
    }
}
