package com.inetum.demo.controllers.onetomany;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OneToManyControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void oneToManyIndexReturnsGendersWithBooks() throws Exception {
        mockMvc.perform(get("/api/v1/onetomany/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fantasía"))
                .andExpect(jsonPath("$[0].books").isArray())
                .andExpect(jsonPath("$[1].name").value("Medieval"));
    }

    @Test
    void oneToManyBiIndexReturnsPersonWithAddresses() throws Exception {
        mockMvc.perform(get("/api/v1/onetomanybi/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("David"))
                .andExpect(jsonPath("$[0].addresses[0].city").value("Salamanca"));
    }

    @Test
    void oneToManyBiAddressReturnsAddressesWithPerson() throws Exception {
        mockMvc.perform(get("/api/v1/onetomanybi/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].street").value("Mayor"))
                .andExpect(jsonPath("$[0].city").value("Salamanca"));
    }
}
