package com.inetum.demo.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void datoSimpleReturnsDato() throws Exception {
        mockMvc.perform(get("/dato-simple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cadena").value("Hola"));
    }

    @Test
    void datoResponseReturnsDato() throws Exception {
        mockMvc.perform(get("/dato-response"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cadena").value("Hola"));
    }

    @Test
    void datoJsonReturnsHandmadeJsonString() throws Exception {
        mockMvc.perform(get("/dato-json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\"=1, \"cadena\"=\"Hola\"}"));
    }
}
