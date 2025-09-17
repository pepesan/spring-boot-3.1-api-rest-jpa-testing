package com.inetum.demo.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.demo.domain.Alumno;
import com.inetum.demo.dtos.AlumnoDTO;
import com.inetum.demo.repositories.AlumnoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AlumnoRepositoryControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    public String basePath = "/api/v1/repository";

    @Autowired
    AlumnoRepository alumnoRepository;

    @BeforeEach
    void seedData() {
        alumnoRepository.deleteAll();

        List<Alumno> batch = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            AlumnoDTO dto = new AlumnoDTO();
            dto.setNombre("Nombre" + i);
            dto.setApellidos("Apellido" + i);
            dto.setEdad(18 + (i % 5));
            // MUY IMPORTANTE: usamos el ctor Alumno(AlumnoDTO) para que id sea null (no 0L)
            batch.add(new Alumno(dto));
        }
        alumnoRepository.saveAll(batch);
    }

    @AfterEach
    void cleanData() {
        alumnoRepository.deleteAll();
    }
    @Test
    void testListShouldReturnOkResult() throws Exception {
        mockMvc.perform(
                        get(basePath+"/")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void search_defaults_should_return_first_page_of_10() throws Exception {
        mockMvc.perform(get(basePath + "/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0))          // 0-based en la respuesta
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].nombre").value("Nombre1"))
                .andExpect(jsonPath("$.content[9].nombre").value("Nombre10"));
    }

    @Test
    void search_custom_page2_size5_should_return_items_6_to_10() throws Exception {
        mockMvc.perform(get(basePath + "/search")
                        .param("page", "2")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].nombre").value("Nombre6"))
                .andExpect(jsonPath("$.content[4].nombre").value("Nombre10"));
    }

    @Test
    void search_should_cap_size_to_100_but_return_only_existing_content() throws Exception {
        mockMvc.perform(get(basePath + "/search")
                        .param("page", "1")
                        .param("size", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(12))
                .andExpect(jsonPath("$.content.length()").value(12))
                .andExpect(jsonPath("$.content[0].nombre").value("Nombre1"))
                .andExpect(jsonPath("$.content[11].nombre").value("Nombre12"));
    }

    @Test
    void search_page_zero_or_negative_should_be_treated_as_first_page() throws Exception {
        mockMvc.perform(get(basePath + "/search")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content.length()").value(10))
                .andExpect(jsonPath("$.content[0].nombre").value("Nombre1"));

        mockMvc.perform(get(basePath + "/search")
                        .param("page", "-3")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.content[0].nombre").value("Nombre1"));
    }
}
