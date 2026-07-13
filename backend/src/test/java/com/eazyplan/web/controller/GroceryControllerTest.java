package com.eazyplan.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración de la API REST de listas de la compra.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class GroceryControllerTest {

    @Autowired
    MockMvc mockMvc;

    private Long registerUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"shopper_%s\",\"name\":\"Shopper\",\"email\":\"s_%s@test.com\",\"password\":\"secret123\"}"
                        .formatted(System.nanoTime(), System.nanoTime())))
                .andReturn();
        return Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long createList(Long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/{userId}/grocery-lists", userId))
                .andExpect(status().isCreated())
                .andReturn();
        return Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void createList_returns201_withDefaults() throws Exception {
        Long userId = registerUser();
        Long listId = createList(userId);

        mockMvc.perform(get("/api/grocery-lists/{listId}", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listId))
                .andExpect(jsonPath("$.purchased").value(false))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void addItem_returns201_andListedAlphabetically() throws Exception {
        Long userId = registerUser();
        Long listId = createList(userId);

        mockMvc.perform(post("/api/grocery-lists/{listId}/items", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Zucchini\",\"category\":\"Veggie\",\"quantity\":2,\"unit\":\"pieces\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Zucchini"));

        mockMvc.perform(post("/api/grocery-lists/{listId}/items", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Almonds\",\"category\":\"Nuts\",\"quantity\":500,\"unit\":\"g\",\"organic\":true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organic").value(true));

        mockMvc.perform(get("/api/grocery-lists/{listId}/items", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Almonds"))   // ordenado por nombre
                .andExpect(jsonPath("$[1].name").value("Zucchini"));
    }

    @Test
    void markPurchased_flipsFlag() throws Exception {
        Long userId = registerUser();
        Long listId = createList(userId);

        mockMvc.perform(patch("/api/grocery-lists/{listId}/purchase", listId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchased").value(true));
    }

    @Test
    void deleteList_returns204() throws Exception {
        Long userId = registerUser();
        Long listId = createList(userId);

        mockMvc.perform(delete("/api/grocery-lists/{listId}", listId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/grocery-lists/{listId}", listId))
                .andExpect(status().isNotFound());
    }
}
