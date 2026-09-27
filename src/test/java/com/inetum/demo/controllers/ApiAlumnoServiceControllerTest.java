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

@SpringBootTest
@AutoConfigureMockMvc
class ApiAlumnoServiceControllerTest {

    private static final String BASE_PATH = "/api/v1/alumnos";

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
        String body = mockMvc.perform(post(BASE_PATH + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"" + nombre + "\",\"apellidos\":\"Perez\",\"edad\":20}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = mapper.readTree(body);
        return node.get("id").asLong();
    }

    @Test
    void createFindUpdateAndDeleteAlumno() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(get(BASE_PATH + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Marta')]").exists());

        mockMvc.perform(get(BASE_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marta"));

        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Marta2\",\"apellidos\":\"Perez\",\"edad\":22}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marta2"));

        mockMvc.perform(delete(BASE_PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marta2"));

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

    @Test
    void searchReturnsPageOfAlumnos() throws Exception {
        crearAlumno("Uno");
        crearAlumno("Dos");

        mockMvc.perform(get(BASE_PATH + "/search/0/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
