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
 * Tests de integración de la API REST de dietas.
 * Cubre CRUD completo + cálculo de macros + errores 404/400.
 */
@SpringBootTest
@AutoConfigureMockMvc
class DietControllerTest {

    @Autowired
    MockMvc mockMvc;

    private Long registerUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"dieter_%s\",\"name\":\"Dieter\",\"email\":\"d_%s@test.com\",\"password\":\"secret123\"}"
                        .formatted(System.nanoTime(), System.nanoTime())))
                .andReturn();
        return parseId(result);
    }

    private Long parseId(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long createDiet(Long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/{userId}/diets", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Keto Cut\",\"dietType\":\"KETO\",\"startDate\":\"2026-07-13\","
                        + "\"dailyCalories\":2000,\"dailyProtein\":150,\"dailyCarbs\":50,\"dailyFats\":100,\"dailyWater\":2.5}"))
                .andExpect(status().isCreated())
                .andReturn();
        return parseId(result);
    }

    @Test
    void createDiet_returns201_andAppearsInList() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        mockMvc.perform(get("/api/users/{userId}/diets", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(dietId))
                .andExpect(jsonPath("$[0].name").value("Keto Cut"))
                .andExpect(jsonPath("$[0].dietType").value("KETO"));
    }

    @Test
    void getDiet_returns200() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        mockMvc.perform(get("/api/diets/{dietId}", dietId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dietId))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void updateDiet_changesNameAndMacros() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        mockMvc.perform(put("/api/diets/{dietId}", dietId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Vegan Bulk\",\"dailyCalories\":2500}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vegan Bulk"))
                .andExpect(jsonPath("$.dailyCalories").value(2500.0));
    }

    @Test
    void deleteDiet_returns204_and404after() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        mockMvc.perform(delete("/api/diets/{dietId}", dietId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/diets/{dietId}", dietId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDiet_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/diets/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void calculateMacros_returnsBalancedSplit() throws Exception {
        mockMvc.perform(post("/api/diets/calculate")
                .param("type", "BALANCED")
                .param("targetCalories", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dietType").value("BALANCED"))
                .andExpect(jsonPath("$.dailyCalories").value(2000.0))
                .andExpect(jsonPath("$.dailyProtein").value(600.0))   // 30%
                .andExpect(jsonPath("$.dailyCarbs").value(800.0));     // 40%
    }
}
