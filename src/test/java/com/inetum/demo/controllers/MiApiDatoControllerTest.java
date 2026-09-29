package com.inetum.demo.controllers;

import tools.jackson.databind.json.JsonMapper;
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
}
