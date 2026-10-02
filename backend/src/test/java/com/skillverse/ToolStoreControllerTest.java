package com.skillverse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ToolStoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetProductsAndCategories() throws Exception {
        mockMvc.perform(get("/api/store/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/store/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/store/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").exists());
    }

    @Test
    void testGetMarketplaceItems() throws Exception {
        mockMvc.perform(get("/api/marketplace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
