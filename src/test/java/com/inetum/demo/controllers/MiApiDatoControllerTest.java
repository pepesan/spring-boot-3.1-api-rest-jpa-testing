package com.inetum.demo.controllers;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import com.inetum.demo.dtos.Dato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// Métodos de mockMvc.perform
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
@SpringBootTest
@AutoConfigureMockMvc
//Given
public class MiApiDatoControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    JsonMapper mapper;
    public String basePath = "/api/dato";

    @BeforeEach
    public void clearRestData() throws Exception {
        System.out.println("limpiando");
        mockMvc.perform(get(basePath +"/clear")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnOkResult() throws Exception {
        mockMvc.perform(get(basePath)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnDatoOkResult() throws Exception {
        List<Dato> listadoEsperado= new ArrayList<Dato>();
        mockMvc.perform(get(basePath)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // comprobación del tipo de contenido
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // comprobación del contenido
                .andExpect(content().json(mapper.writeValueAsString(listadoEsperado)));
    }
    @Test
    void testAddShouldReturnDato() throws Exception {
        Dato datoEnviado = new Dato(0L,"valor");
        Dato datoPrevisto = new Dato(1L,"valor");
        // When
        mockMvc.perform(post(basePath)
                                .content(asJsonString(datoEnviado))
                                .contentType(MediaType.APPLICATION_JSON))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(datoPrevisto)));
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

        // Valor previsto a devolver
        Dato datoPrevisto = new Dato(1L,"valor");
        mockMvc.perform(get(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(datoPrevisto)));
    }

    @Test
    void testUpdateShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        Dato datoEnviado = new Dato(0L,"valor1");
        Dato datoPrevisto = new Dato(1L,"valor1");
        mockMvc.perform(put(basePath+"/1")
                                .content(asJsonString(datoEnviado))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(datoPrevisto)));
    }
    @Test
    void testRemoveByIDShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        Dato datoPrevisto = new Dato(1L,"valor");
        mockMvc.perform(delete(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(datoPrevisto)));
    }

    @Test
    void testRemoveByIDShouldReturnEmptyDatoWhenNotFound() throws Exception {
        mockMvc.perform(delete(basePath + "/999")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato())));
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
                                .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
        // el cambio queda guardado
        mockMvc.perform(get(basePath + "/1"))
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
    }

    @Test
    void testPatchWithEmptyBodyShouldKeepDato() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1").content(mapper.writeValueAsString(cuerpoPatch())).contentType(MERGE_PATCH))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor"))));
    }

    @Test
    void testPatchShouldIgnoreIdChange() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                                .content(mapper.writeValueAsString(cuerpoPatch().put("id", 99).put("cadena", "valor1")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor1"))));
    }

    @Test
    void testPatchWithInvalidValueShouldReturn400AndKeepDato() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                                .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "abc")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cadena").value("Debe tener entre 4 y 20 chars"));
        mockMvc.perform(get(basePath + "/1"))
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor"))));
    }

    @Test
    void testPatchWithNullValueShouldReturn400() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                                .content(mapper.writeValueAsString(cuerpoPatch().putNull("cadena")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPatchWithUnknownFieldShouldBeIgnored() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                                .content(mapper.writeValueAsString(cuerpoPatch().put("inexistente", "x")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(new Dato(1L, "valor"))));
    }

    @Test
    void testPatchNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(patch(basePath + "/999")
                                .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1")))
                                .contentType(MERGE_PATCH))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Not found with id = 999"));
    }

    @Test
    void testPatchWithPlainJsonContentTypeShouldReturn415() throws Exception {
        testAddShouldReturnDato();
        mockMvc.perform(patch(basePath + "/1")
                                .content(mapper.writeValueAsString(cuerpoPatch().put("cadena", "valor1")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }
}
