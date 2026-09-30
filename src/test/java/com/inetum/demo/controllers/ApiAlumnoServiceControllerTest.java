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
import tools.jackson.databind.node.ObjectNode;

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

    private static final String MERGE_PATCH = "application/merge-patch+json";

    /** Objeto JSON vacío al que se añaden solo los campos a modificar. */
    private ObjectNode cuerpoPatch() {
        return mapper.createObjectNode();
    }

    @Test
    void patchShouldModifyOnlySentFields() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(patch(BASE_PATH + "/" + id)
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("edad", 30))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Marta"))
                .andExpect(jsonPath("$.apellidos").value("Perez"))
                .andExpect(jsonPath("$.edad").value(30));

        // el cambio queda guardado
        mockMvc.perform(get(BASE_PATH + "/" + id))
                .andExpect(jsonPath("$.nombre").value("Marta"))
                .andExpect(jsonPath("$.edad").value(30));
    }

    @Test
    void patchWithInvalidValueShouldReturn400AndKeepAlumno() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(patch(BASE_PATH + "/" + id)
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("edad", 10))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.edad").value("el usuario debe tener 18+"));

        mockMvc.perform(get(BASE_PATH + "/" + id))
                .andExpect(jsonPath("$.edad").value(20));
    }

    @Test
    void patchNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(patch(BASE_PATH + "/999999")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("edad", 30))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void patchWithUnknownFieldShouldBeIgnored() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(patch(BASE_PATH + "/" + id)
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("inexistente", "x"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marta"))
                .andExpect(jsonPath("$.edad").value(20))
                .andExpect(jsonPath("$.inexistente").doesNotExist());
    }

    @Test
    void patchWithPlainJsonContentTypeShouldReturn415() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(patch(BASE_PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("edad", 30))))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void patchWithMalformedJsonShouldReturn400() throws Exception {
        long id = crearAlumno("Marta");

        mockMvc.perform(patch(BASE_PATH + "/" + id)
                        .contentType(MERGE_PATCH)
                        .content("{\"edad\": 30,}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Petición mal formada"));
    }
}
