package com.inetum.demo.controllers.herencia;

import com.inetum.demo.repositories.herencia.PortatilRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** CRUD completo de Portatil con PortatilDTO en POST/PUT. */
@SpringBootTest
@AutoConfigureMockMvc
class PortatilControllerTest {

    private static final String BASE = "/api/v1/portatiles";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private PortatilRepository portatilRepository;

    @BeforeEach
    void limpiar() {
        portatilRepository.deleteAll();
    }

    private static String json(String marca, String modelo, double peso, int bateria) {
        return "{\"marca\":\"" + marca + "\",\"modelo\":\"" + modelo
                + "\",\"peso\":" + peso + ",\"duracionBateria\":" + bateria + "}";
    }

    private long crear(String marca, String modelo) throws Exception {
        MvcResult r = mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(marca, modelo, 1.4, 10)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void createDevuelve201ConElPortatil() throws Exception {
        mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Lenovo", "T14", 1.5, 12)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.marca").value("Lenovo"))
                .andExpect(jsonPath("$.peso").value(1.5))
                .andExpect(jsonPath("$.duracionBateria").value(12));
    }

    @Test
    void createInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post(BASE + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("", "T14", -1, -5)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void indexListaLosPortatiles() throws Exception {
        crear("Lenovo", "T14");
        crear("Dell", "XPS");
        mockMvc.perform(get(BASE + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void showDevuelveElPortatilYNotFoundSiNoExiste() throws Exception {
        long id = crear("Lenovo", "T14");
        mockMvc.perform(get(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modelo").value("T14"));
        mockMvc.perform(get(BASE + "/" + (id + 1000)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putReemplazaLosCamposYMantieneElId() throws Exception {
        long id = crear("Lenovo", "T14");
        mockMvc.perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Dell", "XPS", 1.2, 15)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.marca").value("Dell"))
                .andExpect(jsonPath("$.duracionBateria").value(15));
        mockMvc.perform(get(BASE + "/" + id))
                .andExpect(jsonPath("$.modelo").value("XPS"));
    }

    @Test
    void putInexistenteDevuelve404YInvalidoDevuelve400() throws Exception {
        long id = crear("Lenovo", "T14");
        mockMvc.perform(put(BASE + "/" + (id + 1000))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Dell", "XPS", 1.2, 15)))
                .andExpect(status().isNotFound());
        mockMvc.perform(put(BASE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Dell", "", 1.2, 15)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBorraYDespuesNoExiste() throws Exception {
        long id = crear("Lenovo", "T14");
        mockMvc.perform(delete(BASE + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mockMvc.perform(get(BASE + "/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete(BASE + "/" + id)).andExpect(status().isNotFound());
    }
}
