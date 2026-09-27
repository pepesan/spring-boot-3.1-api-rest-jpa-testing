package com.inetum.demo.controllers.onetoone;

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
class OneToOneControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void oneToOneIndexReturnsPhoneWithDetails() throws Exception {
        mockMvc.perform(get("/api/v1/onetoone/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].number").value("923124578"))
                .andExpect(jsonPath("$[0].details.provider").value("PepePhone"))
                .andExpect(jsonPath("$[0].details.technology").value("5G"));
    }

    @Test
    void oneToOneProviderReturnsPepephones() throws Exception {
        // /provider consulta sin sembrar datos: hay que llamar antes a "/" (que sí los siembra).
        mockMvc.perform(get("/api/v1/onetoone/"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/v1/onetoone/provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].details.provider").value("PepePhone"));
    }

    @Test
    void oneToOneBidirectionalIndexReturnsOrderWithBillingAddress() throws Exception {
        mockMvc.perform(get("/api/v1/onetoonebi/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("H0ck"))
                .andExpect(jsonPath("$[0].billingAddress.street").value("Plaza Mayor"));
    }

    @Test
    void oneToOneBidirectionalAddressReturnsAddressWithOrder() throws Exception {
        mockMvc.perform(get("/api/v1/onetoonebi/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].street").value("Plaza Mayor"))
                .andExpect(jsonPath("$[0].order.code").value("H0ck"));
    }
}
