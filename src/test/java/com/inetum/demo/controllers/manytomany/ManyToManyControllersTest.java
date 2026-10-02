package com.inetum.demo.controllers.manytomany;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ManyToManyControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bidirectionalIndexReturnsUserWithRoles() throws Exception {
        mockMvc.perform(get("/api/v1/manytomany/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("David"))
                .andExpect(jsonPath("$[0].roles[0].name").value("Admin"));
    }

    @Test
    void bidirectionalRolesReturnsRoles() throws Exception {
        mockMvc.perform(get("/api/v1/manytomany/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin"));
    }

    @Test
    void bidirectionalListadoReturnsSeededUsers() throws Exception {
        // listado()/listadoRoles() solo consultan: hay que sembrar antes con "/".
        mockMvc.perform(get("/api/v1/manytomany/"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/manytomany/listado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("David"));
        mockMvc.perform(get("/api/v1/manytomany/listadoRoles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Admin"));
    }

    @Test
    void unidirectionalIndexReturnsNoticiaWithEtiquetas() throws Exception {
        mockMvc.perform(get("/api/v1/manytomanyuni/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Noticia 1"))
                // etiquetas es un Set (HashSet): no tiene orden garantizado, se comprueba sin depender de él
                .andExpect(jsonPath("$[0].etiquetas[*].nombre",
                        containsInAnyOrder("Etiqueta 1", "Etiqueta 2")));
    }
}
