package com.dayzer0.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The tests shrink the dataset and zero the simulated round-trip so they stay
 * fast; the slow defaults exist for the running server, not for ./mvnw test.
 */
@SpringBootTest(properties = {"app.seed-rows=100", "app.db-delay-ms=0"})
@AutoConfigureMockMvc
class CatalogApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthAnswersOk() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void productOneIsFound() throws Exception {
        mvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void unknownProductIs404() throws Exception {
        mvc.perform(get("/products/99999")).andExpect(status().isNotFound());
    }
}
