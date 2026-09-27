package com.inetum.demo.controllers;

import com.inetum.demo.dtos.ValidadoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomValidationControllerTest {

    private static final String BASE_PATH = "/api/v1/custom";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void aValidUsernameIsAccepted() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"david\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("david"));
    }

    @Test
    void aNullUsernameIsAccepted() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void theForbiddenUsernameIsRejected() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validadoDtoGettersAndSettersWork() {
        ValidadoDTO dto = new ValidadoDTO();
        dto.setUsername("david");
        org.assertj.core.api.Assertions.assertThat(dto.getUsername()).isEqualTo("david");
    }
}
