package com.inetum.demo.controllers.criteria;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EmpleadoCriteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void buscarEmpleadosPorNombreYEdadFiltraPorEdad() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Elena\",\"edad\":33,\"dni\":\"9Z\",\"sueldo\":22000}"));

        mockMvc.perform(get("/api/v1/empleados-criteria/nombre/Elena/edad/33"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Elena')]").exists());
    }
}
