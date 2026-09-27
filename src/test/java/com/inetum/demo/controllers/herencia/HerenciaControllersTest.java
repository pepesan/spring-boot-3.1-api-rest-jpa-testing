package com.inetum.demo.controllers.herencia;

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
class HerenciaControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAndListEmpleados() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"direccion\":\"Calle Mayor\",\"edad\":30,\"dni\":\"1A\",\"sueldo\":25000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.sueldo").value(25000));

        // No hay endpoint de "clear": la tabla se comparte con otros tests de la misma clase,
        // así que se busca por filtro en vez de asumir una posición fija.
        mockMvc.perform(get("/api/v1/empleados/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Ana')]").exists());
    }

    @Test
    void empleadosConSueldoMayorAFiltraCorrectamente() throws Exception {
        mockMvc.perform(post("/api/v1/empleados/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Bajo\",\"edad\":25,\"dni\":\"2B\",\"sueldo\":15000}"));
        mockMvc.perform(post("/api/v1/empleados/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Alto\",\"edad\":40,\"dni\":\"3C\",\"sueldo\":35000}"));

        mockMvc.perform(get("/api/v1/empleados/sueldo-mayor/20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre=='Alto')]").exists())
                .andExpect(jsonPath("$[?(@.nombre=='Bajo')]").doesNotExist());
    }

    @Test
    void createAndListJefes() throws Exception {
        mockMvc.perform(post("/api/v1/jefes/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Carlos\",\"dni\":\"4D\",\"sueldo\":50000,\"departamento\":\"IT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos"));

        mockMvc.perform(get("/api/v1/jefes/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.departamento=='IT')]").exists());
    }
}
