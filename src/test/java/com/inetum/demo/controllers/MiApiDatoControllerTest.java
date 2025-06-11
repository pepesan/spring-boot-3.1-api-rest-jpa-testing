package com.inetum.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.demo.dtos.Dato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MiApiDatoControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    public String basePath = "/api/dato";

    @BeforeEach
    public void clearRestData() throws Exception {
        System.out.println("limpiando");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath +"/clear")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnOkResult() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testListShouldReturnDatoOkResult() throws Exception {
        List<Dato> listadoEsperado= new ArrayList<Dato>();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // comprobación del tipo de contenido
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // comprobación del contenido
                .andExpect(content().json(mapper.writeValueAsString(listadoEsperado)));
    }
    @Test
    void testAddShouldReturnDato() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(basePath)
                                .content(asJsonString(new Dato(0L,"valor")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(new
                        Dato(1L,"valor"))));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetByIDShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(new
                        Dato(1L,"valor"))));
    }

    @Test
    void testUpdateShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(basePath+"/1")
                                .content(asJsonString(new Dato(0L,"valor1")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(new
                        Dato(1L,"valor1"))));
    }
    @Test
    void testRemoveByIDShouldReturnDato() throws Exception {
        // metemos el dato antes de consultarlo
        testAddShouldReturnDato();
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(basePath+"/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(new
                        Dato(1L,"valor"))));
    }
}
