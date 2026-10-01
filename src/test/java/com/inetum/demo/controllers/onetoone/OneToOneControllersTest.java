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
