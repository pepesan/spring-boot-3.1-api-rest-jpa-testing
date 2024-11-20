package com.inetum.demo.ejercicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
public class ClienteControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    public String basePath = "/api/v1/cliente";
    @Autowired
    private ClienteRepository clienteRepository;
    @BeforeEach
    void setUp() {
        this.clienteRepository.deleteAll();
    }
    @Test
    void testListShouldReturnOkResult() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath+"/")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testAddShouldReturnDato() throws Exception {
        System.out.println("añadiendo");
        mockMvc.perform(
                        // configura la petición
                        //MockMvcRequestBuilders.
                        post(basePath+"/")
                                .content(asJsonString(new ClienteDTO("Pilar", "p@p.com", "Madrid")))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Pilar"))
                .andExpect(jsonPath("$.email").value("p@p.com"))
                .andExpect(jsonPath("$.dir").value("Madrid"))
                .andExpect(jsonPath("$.id").exists());
    }
    @Test
    void testAddShouldReturnDato2() throws Exception {
        System.out.println("añadiendo");
        mockMvc.perform(
                        // configura la petición
                        //MockMvcRequestBuilders.
                        post(basePath+"/")
                                .content(asJsonString(new ClienteDTO("Pilar", "p@p.com", "Madrid")))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Pilar"))
                .andExpect(jsonPath("$.email").value("p@p.com"))
                .andExpect(jsonPath("$.dir").value("Madrid"))
                .andExpect(jsonPath("$.id").exists());
    }
    @Test
    void testGetByIDShouldReturnOkResult() throws Exception {
        Cliente clienteGuardado = this.clienteRepository.save(new Cliente(1L, "Pilar", "p@.com", "Madrid"));
        System.out.println(clienteGuardado);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath+"/"+clienteGuardado.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void testGetByIDShouldReturnNotFoundResult() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(basePath+"/300")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testUpdateByIDShouldReturnOkResult() throws Exception {
        Cliente clienteGuardado = this.clienteRepository.save(new Cliente(1L, "Pilar", "p@.com", "Madrid"));
        System.out.println(clienteGuardado);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(basePath+"/"+clienteGuardado.getId())
                                .content(asJsonString(new ClienteDTO("Pilar2", "p2@p.com", "Madrid2")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Pilar2"))
                .andExpect(jsonPath("$.email").value("p2@p.com"))
                .andExpect(jsonPath("$.dir").value("Madrid2"))
                .andExpect(jsonPath("$.id").exists());
    }
    @Test
    void testUpdateByIDShouldReturnNotFoundResult() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .put(basePath+"/300")
                                .content(asJsonString(new ClienteDTO("Pilar2", "p2@p.com", "Madrid2")))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    void testDeleteByIDShouldReturnOkResult() throws Exception {
        Cliente clienteGuardado = this.clienteRepository.save(new Cliente(1L, "Pilar", "p@.com", "Madrid"));
        System.out.println(clienteGuardado);
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(basePath+"/"+clienteGuardado.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void testDeleteByIDShouldReturnNotFoundResult() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(basePath+"/300")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
