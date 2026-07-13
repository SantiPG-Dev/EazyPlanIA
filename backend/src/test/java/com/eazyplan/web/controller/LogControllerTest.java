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
 * Tests de integración de la API REST de logs de macro- y micronutrientes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class LogControllerTest {

    @Autowired
    MockMvc mockMvc;

    private Long registerUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"tracker_%s\",\"name\":\"Tracker\",\"email\":\"t_%s@test.com\",\"password\":\"secret123\"}"
                        .formatted(System.nanoTime(), System.nanoTime())))
                .andReturn();
        return Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long createDiet(Long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/{userId}/diets", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Base\",\"dietType\":\"BALANCED\",\"startDate\":\"2026-07-13\","
                        + "\"dailyCalories\":2000,\"dailyProtein\":150,\"dailyCarbs\":200,\"dailyFats\":60,\"dailyWater\":2.0}"))
                .andReturn();
        return Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void createMacroLog_returns201_linkedToDiet() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        mockMvc.perform(post("/api/users/{userId}/macro-logs", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dietId\":%d,\"date\":\"2026-07-13\",\"calories\":1850,\"protein\":140,\"carbs\":180,\"fats\":55}"
                        .formatted(dietId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.dietId").value(dietId))
                .andExpect(jsonPath("$.calories").value(1850.0));

        mockMvc.perform(get("/api/users/{userId}/macro-logs", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calories").value(1850.0));
    }

    @Test
    void createMacroLog_unknownDiet_returns404() throws Exception {
        Long userId = registerUser();

        mockMvc.perform(post("/api/users/{userId}/macro-logs", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dietId\":99999,\"date\":\"2026-07-13\",\"calories\":100}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMacroLog_returns204() throws Exception {
        Long userId = registerUser();
        Long dietId = createDiet(userId);

        MvcResult result = mockMvc.perform(post("/api/users/{userId}/macro-logs", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dietId\":%d,\"date\":\"2026-07-13\",\"calories\":100}".formatted(dietId)))
                .andReturn();
        Long logId = Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/api/macro-logs/{id}", logId))
                .andExpect(status().isNoContent());
    }

    @Test
    void createMicroLog_returns201() throws Exception {
        Long userId = registerUser();

        mockMvc.perform(post("/api/users/{userId}/micro-logs", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-07-13\",\"sodium\":2300,\"potassium\":3500,"
                        + "\"magnesium\":400,\"calcium\":1000,\"iron\":18,\"vitaminC\":90}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.sodium").value(2300.0))
                .andExpect(jsonPath("$.vitaminC").value(90.0));

        mockMvc.perform(get("/api/users/{userId}/micro-logs", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].iron").value(18.0));
    }

    @Test
    void deleteMicroLog_returns204() throws Exception {
        Long userId = registerUser();

        MvcResult result = mockMvc.perform(post("/api/users/{userId}/micro-logs", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-07-13\",\"sodium\":100}"))
                .andReturn();
        Long logId = Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));

        mockMvc.perform(delete("/api/micro-logs/{id}", logId))
                .andExpect(status().isNoContent());
    }
}
