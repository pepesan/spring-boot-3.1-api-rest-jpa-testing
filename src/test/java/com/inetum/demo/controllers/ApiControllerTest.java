package com.inetum.demo.controllers;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import java.util.ArrayList;
import java.util.List;
import static
        org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JsonMapper mapper;
    public String basePath = "/api/v1/dato";

    @Autowired
    private APIController controller;

    @BeforeEach
    public void clearRestData() throws Exception {
        System.out.println("limpiando");
        mockMvc.perform(
                        get(basePath+"/clear")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnOkResult() throws Exception {
        mockMvc.perform(
                        get(basePath+"/")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnOkResultEmpty() throws Exception {
        List<Dato> listadoEsperado= new ArrayList<Dato>();
        mockMvc.perform(
                        get(basePath+"/")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // comprobación del tipo de contenido
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // comprobación del contenido
                .andExpect(content().json(mapper.writeValueAsString(listadoEsperado)));
    }

    @Test
    void testAddShouldReturnDato() throws Exception {
        System.out.println("añadiendo");
        mockMvc.perform(
                        // configura la petición
                        //MockMvcRequestBuilders.
                        post(basePath+"/")
                                .content(asJsonString(new DatoDTO("valor")))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(new Dato(1L,"valor"))));
    }
    public static String asJsonString(final Object obj) {
        try {
            return new JsonMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetByIDShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        get(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(new Dato(1L,"valor"))));
    }
    @Test
    void testGetByIDShouldNotReturnDato() throws Exception {
        mockMvc.perform(
                       get(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testUpdateShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        put(basePath+"/1")
                                .content(asJsonString(new DatoDTO("valor1")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(new Dato(1L,"valor1"))));
    }
    @Test
    void testUpdateShouldNotReturnDato() throws Exception {
        mockMvc.perform(
                        put(basePath+"/1")
                                .content(asJsonString(new DatoDTO("valor1")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testRemoveByIDShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        delete(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(new Dato(1L,"valor"))));
    }
    @Test
    void testRemoveByIDShouldNotReturnDato() throws Exception {
        mockMvc.perform(
                        delete(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Not found with id = 1"))
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private static final String MERGE_PATCH = "application/merge-patch+json";

    /** Objeto JSON vacío al que se añaden solo los campos a modificar. */
    private ObjectNode cuerpoPatch() {
        return mapper.createObjectNode();
    }

    @Test
    void testPatchShouldModifyOnlySentFields() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
        // el cambio queda guardado
        mockMvc.perform(get(basePath + "/1"))
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
    }

    @Test
    void testPatchShouldIgnoreIdChange() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("id", 99).put("cadena", "valor1"))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
    }

    @Test
    void testPatchWithInvalidValueShouldReturn400AndKeepDato() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "abc"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cadena").value("Debe tener entre 4 y 20 chars"));
        mockMvc.perform(get(basePath + "/1"))
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor"))));
    }

    @Test
    void testPatchNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(patch(basePath + "/999")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Not found with id = 999"));
    }

    @Test
    void testPatchWithUnknownFieldShouldBeIgnored() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MERGE_PATCH)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("inexistente", "x"))))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor"))));
    }

    @Test
    void testPatchWithPlainJsonContentTypeShouldReturn415() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1"))))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testPatchWithMalformedJsonShouldReturn400() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                        .contentType(MERGE_PATCH)
                        .content("{\"cadena\": \"adios\",}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Petición mal formada"));
    }
}
