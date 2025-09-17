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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    /* =============== Helpers =============== */

    private Long crearAlumnoYDevolverId(String nombre, String apellidos, int edad) throws Exception {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre(nombre);
        dto.setApellidos(apellidos);
        dto.setEdad(edad);

        String json = mapper.writeValueAsString(dto);

        String response = mockMvc.perform(
                        post(basePath + "/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value(nombre))
                .andExpect(jsonPath("$.apellidos").value(apellidos))
                .andExpect(jsonPath("$.edad").value(edad))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return mapper.readTree(response).get("id").asLong();
    }
    /* =============== CREATE =============== */

    @Test
    void create_should_persist_and_return_entity() throws Exception {
        Long id = crearAlumnoYDevolverId("Carlos", "López", 22);

        // Verificamos con un GET por id que realmente se guardó
        mockMvc.perform(get(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("López"))
                .andExpect(jsonPath("$.edad").value(22));
    }

    @Test
    void create_should_fail_validation_when_nombre_short_or_edad_under_18() throws Exception {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setNombre("Jo");              // < 3 chars
        dto.setApellidos("Corto");
        dto.setEdad(16);                  // < 18
        String json = mapper.writeValueAsString(dto);

        mockMvc.perform(post(basePath + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    /* =============== READ BY ID =============== */

    @Test
    void show_should_return_404_when_not_found() throws Exception {
        mockMvc.perform(get(basePath + "/999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void show_should_return_entity_when_exists() throws Exception {
        Long id = crearAlumnoYDevolverId("Ana", "Pérez", 20);

        mockMvc.perform(get(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellidos").value("Pérez"))
                .andExpect(jsonPath("$.edad").value(20));
    }

    /* =============== UPDATE BY ID =============== */

    @Test
    void update_should_modify_and_return_entity() throws Exception {
        Long id = crearAlumnoYDevolverId("Luis", "García", 19);

        AlumnoDTO update = new AlumnoDTO();
        update.setNombre("Luis Miguel");
        update.setApellidos("García Ruiz");
        update.setEdad(23);
        String jsonUpdate = mapper.writeValueAsString(update);

        mockMvc.perform(put(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Luis Miguel"))
                .andExpect(jsonPath("$.apellidos").value("García Ruiz"))
                .andExpect(jsonPath("$.edad").value(23));

        // Comprobación con GET
        mockMvc.perform(get(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Luis Miguel"))
                .andExpect(jsonPath("$.apellidos").value("García Ruiz"))
                .andExpect(jsonPath("$.edad").value(23));
    }

    @Test
    void update_should_return_404_when_entity_not_found() throws Exception {
        AlumnoDTO update = new AlumnoDTO();
        update.setNombre("No Existe");
        update.setApellidos("Nada");
        update.setEdad(30);
        String jsonUpdate = mapper.writeValueAsString(update);

        mockMvc.perform(put(basePath + "/123456")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_should_fail_validation_when_payload_invalid() throws Exception {
        Long id = crearAlumnoYDevolverId("Valido", "Usuario", 21);

        AlumnoDTO invalid = new AlumnoDTO();
        invalid.setNombre("AB");  // inválido: < 3
        invalid.setApellidos("Usuario");
        invalid.setEdad(17);      // inválido: < 18
        String jsonInvalid = mapper.writeValueAsString(invalid);

        mockMvc.perform(put(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalid))
                .andExpect(status().isBadRequest());
    }

    /* =============== DELETE BY ID =============== */

    @Test
    void delete_should_remove_entity_and_return_it() throws Exception {
        Long id = crearAlumnoYDevolverId("Borrar", "Esto", 25);

        mockMvc.perform(delete(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Borrar"));

        // Tras borrar debe devolver 404
        mockMvc.perform(get(basePath + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_should_return_404_when_not_found() throws Exception {
        mockMvc.perform(delete(basePath + "/987654")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
