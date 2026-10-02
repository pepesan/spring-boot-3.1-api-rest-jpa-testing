package com.inetum.demo.controllers.herencia.singletable;

import com.inetum.demo.repositories.herencia.singletable.VehiculoRepository;
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

/** Herencia SINGLE_TABLE: Coche, Moto y Camion en la tabla "vehiculos" con CRUD por tipo y vista polimórfica. */
@SpringBootTest
@AutoConfigureMockMvc
class VehiculoControllersTest {

    private static final String COCHE = "{\"marca\":\"Seat\",\"modelo\":\"Ibiza\",\"anio\":2020,"
            + "\"numeroPuertas\":5,\"combustible\":\"gasolina\"}";
    private static final String MOTO = "{\"marca\":\"Yamaha\",\"modelo\":\"MT-07\",\"anio\":2021,"
            + "\"cilindrada\":689,\"tieneSidecar\":false}";
    private static final String CAMION = "{\"marca\":\"Volvo\",\"modelo\":\"FH\",\"anio\":2019,"
            + "\"cargaMaximaKg\":18000.5,\"numeroEjes\":3}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private VehiculoRepository vehiculoRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void limpiar() {
        vehiculoRepository.deleteAll();
    }

    private long crear(String ruta, String json) throws Exception {
        MvcResult r = mockMvc.perform(post("/api/v1/" + ruta + "/")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    private void crearUnoDeCadaTipo() throws Exception {
        crear("coches", COCHE);
        crear("motos", MOTO);
        crear("camiones", CAMION);
    }

    // ---------- una sola tabla ----------

    @Test
    void todosLosTiposSeGuardanEnLaMismaTablaConDiscriminador() throws Exception {
        crearUnoDeCadaTipo();

        List<String> tipos = jdbcTemplate.queryForList(
                "SELECT tipo_vehiculo FROM vehiculos ORDER BY tipo_vehiculo", String.class);

        assertThat(tipos).containsExactly("CAMION", "COCHE", "MOTO");
    }

    @Test
    void lasColumnasDeOtrasSubclasesQuedanANull() throws Exception {
        crear("coches", COCHE);

        Integer cilindrada = jdbcTemplate.queryForObject(
                "SELECT cilindrada FROM vehiculos WHERE tipo_vehiculo = 'COCHE'", Integer.class);
        Integer puertas = jdbcTemplate.queryForObject(
                "SELECT numero_puertas FROM vehiculos WHERE tipo_vehiculo = 'COCHE'", Integer.class);

        assertThat(cilindrada).isNull();
        assertThat(puertas).isEqualTo(5);
    }

    // ---------- vista polimórfica ----------

    @Test
    void vehiculosListaTodosLosTiposConSuCampoTipo() throws Exception {
        crearUnoDeCadaTipo();

        mockMvc.perform(get("/api/v1/vehiculos/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[?(@.tipo == 'COCHE')].combustible").value("gasolina"))
                .andExpect(jsonPath("$[?(@.tipo == 'MOTO')].cilindrada").value(689))
                .andExpect(jsonPath("$[?(@.tipo == 'CAMION')].numeroEjes").value(3));
    }

    @Test
    void vehiculosShowDevuelveCualquierTipoYNotFoundSiNoExiste() throws Exception {
        long id = crear("motos", MOTO);

        mockMvc.perform(get("/api/v1/vehiculos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("MOTO"))
                .andExpect(jsonPath("$.modelo").value("MT-07"));
        mockMvc.perform(get("/api/v1/vehiculos/" + (id + 1000)))
                .andExpect(status().isNotFound());
    }

    @Test
    void vehiculosDeleteBorraCualquierTipo() throws Exception {
        long id = crear("camiones", CAMION);

        mockMvc.perform(delete("/api/v1/vehiculos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get("/api/v1/vehiculos/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete("/api/v1/vehiculos/" + id)).andExpect(status().isNotFound());
    }

    // ---------- consultas por subclase ----------

    @Test
    void cadaControladorSoloListaSuTipo() throws Exception {
        crearUnoDeCadaTipo();
        crear("coches", COCHE);

        mockMvc.perform(get("/api/v1/coches/")).andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/v1/motos/")).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(get("/api/v1/camiones/")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void unIdDeOtroTipoEsNotFoundEnLosControladoresPorTipo() throws Exception {
        long idMoto = crear("motos", MOTO);

        mockMvc.perform(get("/api/v1/coches/" + idMoto)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/camiones/" + idMoto)).andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/coches/" + idMoto)
                        .contentType(MediaType.APPLICATION_JSON).content(COCHE))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/v1/motos/" + idMoto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marca").value("Yamaha"));
    }

    // ---------- altas y modificaciones por tipo ----------

    @Test
    void createDevuelve201ConLosCamposDeCadaTipo() throws Exception {
        mockMvc.perform(post("/api/v1/coches/").contentType(MediaType.APPLICATION_JSON).content(COCHE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.tipo").value("COCHE"))
                .andExpect(jsonPath("$.numeroPuertas").value(5));
        mockMvc.perform(post("/api/v1/motos/").contentType(MediaType.APPLICATION_JSON).content(MOTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("MOTO"))
                .andExpect(jsonPath("$.tieneSidecar").value(false));
        mockMvc.perform(post("/api/v1/camiones/").contentType(MediaType.APPLICATION_JSON).content(CAMION))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("CAMION"))
                .andExpect(jsonPath("$.cargaMaximaKg").value(18000.5));
    }

    @Test
    void putReemplazaLosCamposYMantieneElIdEnCadaTipo() throws Exception {
        long idCoche = crear("coches", COCHE);
        long idMoto = crear("motos", MOTO);
        long idCamion = crear("camiones", CAMION);

        mockMvc.perform(put("/api/v1/coches/" + idCoche).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Seat\",\"modelo\":\"Leon\",\"anio\":2022,"
                                + "\"numeroPuertas\":3,\"combustible\":\"diesel\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idCoche))
                .andExpect(jsonPath("$.modelo").value("Leon"))
                .andExpect(jsonPath("$.combustible").value("diesel"));
        mockMvc.perform(put("/api/v1/motos/" + idMoto).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Yamaha\",\"modelo\":\"MT-09\",\"anio\":2022,"
                                + "\"cilindrada\":890,\"tieneSidecar\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cilindrada").value(890))
                .andExpect(jsonPath("$.tieneSidecar").value(true));
        mockMvc.perform(put("/api/v1/camiones/" + idCamion).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Volvo\",\"modelo\":\"FM\",\"anio\":2022,"
                                + "\"cargaMaximaKg\":12000,\"numeroEjes\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroEjes").value(2));

        // el cambio queda persistido y sigue habiendo una fila por vehículo
        mockMvc.perform(get("/api/v1/coches/" + idCoche)).andExpect(jsonPath("$.modelo").value("Leon"));
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehiculos", Integer.class)).isEqualTo(3);
    }

    @Test
    void putDeUnIdInexistenteDevuelve404EnCadaTipo() throws Exception {
        mockMvc.perform(put("/api/v1/coches/9999").contentType(MediaType.APPLICATION_JSON).content(COCHE))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/motos/9999").contentType(MediaType.APPLICATION_JSON).content(MOTO))
                .andExpect(status().isNotFound());
        mockMvc.perform(put("/api/v1/camiones/9999").contentType(MediaType.APPLICATION_JSON).content(CAMION))
                .andExpect(status().isNotFound());
    }

    // ---------- validación ----------

    @Test
    void datosInvalidosDevuelven400() throws Exception {
        mockMvc.perform(post("/api/v1/coches/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"\",\"modelo\":\"Ibiza\",\"anio\":1800,"
                                + "\"numeroPuertas\":9,\"combustible\":\"\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/motos/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Yamaha\",\"modelo\":\"MT-07\",\"anio\":2021,\"cilindrada\":0}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/v1/camiones/").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"Volvo\",\"modelo\":\"FH\",\"anio\":2019,"
                                + "\"cargaMaximaKg\":-1,\"numeroEjes\":1}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/api/v1/coches/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"marca\":\"\"}"))
                .andExpect(status().isBadRequest());
        assertThat(vehiculoRepository.count()).isZero();
    }
}
