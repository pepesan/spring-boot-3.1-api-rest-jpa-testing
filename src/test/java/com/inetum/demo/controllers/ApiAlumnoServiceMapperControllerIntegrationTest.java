package com.inetum.demo.controllers;

import com.inetum.demo.repositories.AlumnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Igual que {@link AlumnoServiceMapperControllerTest} (que aísla el controlador con un
 * repositorio mockeado) pero contra la base de datos real, para cubrir el flujo completo
 * de creación/consulta/actualización/borrado de /api/v1/alumnomapper.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiAlumnoServiceMapperControllerIntegrationTest {

    private static final String BASE_PATH = "/api/v1/alumnomapper";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper mapper;

    @Autowired
    private AlumnoRepository alumnoRepository;

    @BeforeEach
    void limpiar() {
        alumnoRepository.deleteAll();
    }

    private long crearAlumno(String nombre) throws Exception {
        String body = mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"apellidos\":\"Perez\",\"edad\":20}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = mapper.readTree(body);
        return node.get("id").asLong();
    }

    @Test
    void createFindUpdateAndDeleteAlumno() throws Exception {
        long id = crearAlumno("David");

        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='David')]").exists());

        mockMvc.perform(get(BASE_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David"));

        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"David2\",\"apellidos\":\"Perez\",\"edad\":21}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David2"))
                .andExpect(jsonPath("$.edad").value(21));

        mockMvc.perform(delete(BASE_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("David2"));

        mockMvc.perform(get(BASE_PATH + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdNotFoundReturns404() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNotFoundReturns404() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/999999"))
                .andExpect(status().isNotFound());
    }
}
