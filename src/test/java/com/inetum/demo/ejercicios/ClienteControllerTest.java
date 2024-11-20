package com.inetum.demo.ejercicios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inetum.demo.dtos.Dato;
import com.inetum.demo.dtos.DatoDTO;
import com.inetum.demo.repositories.AlumnoRepository;
import com.mysql.cj.xdevapi.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(content().json(asJsonString(new Cliente(1L,"Pilar", "p@p.com", "Madrid"))));
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
