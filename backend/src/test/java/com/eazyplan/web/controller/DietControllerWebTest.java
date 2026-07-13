package com.eazyplan.web.controller;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.domain.entities.User;
import com.eazyplan.security.JwtAuthFilter;
import com.eazyplan.service.DietService;
import com.eazyplan.web.dto.DietResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de slice HTTP del {@link DietController} con el servicio mockado.
 *
 * <p>Ventaja sobre {@code @SpringBootTest}: arranca en ~0.3s (sin BD ni contexto completo).
 * Verifica routing, serialización JSON y manejo de errores del ControllerAdvice.
 */
@WebMvcTest(DietController.class)
@AutoConfigureMockMvc(addFilters = false)
class DietControllerWebTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean DietService dietService;
    @MockBean JwtAuthFilter jwtAuthFilter; // satisface SecurityConfig en contexto de slice

    private Diet sampleDiet() {
        User user = new User("u", "U", "u@e.com", "h");
        user.setId(1L);
        Diet d = new Diet(user, "Keto", DietType.KETO, LocalDate.of(2026, 7, 13), 2000, 150, 50, 100, 2.5f);
        d.setId(10L);
        return d;
    }

    @Test
    void listUserDiets_returns200_jsonArray() throws Exception {
        when(dietService.getUserDiets(1L)).thenReturn(List.of(sampleDiet()));

        mockMvc.perform(get("/api/users/1/diets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].dietType").value("KETO"));
    }

    @Test
    void createDiet_returns201_withLocation() throws Exception {
        when(dietService.createDiet(any(), eq(1L))).thenReturn(sampleDiet());

        mockMvc.perform(post("/api/users/1/diets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Keto\",\"dietType\":\"KETO\",\"startDate\":\"2026-07-13\","
                        + "\"dailyCalories\":2000}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/diets/10"))
                .andExpect(jsonPath("$.name").value("Keto"));
    }

    @Test
    void getDiet_returns404_whenNotFound() throws Exception {
        when(dietService.getDiet(99L)).thenThrow(new NotFoundException("Diet", 99L));

        mockMvc.perform(get("/api/diets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void calculateMacros_returns200() throws Exception {
        Diet calculated = new Diet();
        calculated.setDietType(DietType.BALANCED);
        calculated.setDailyCalories(2000);
        calculated.setDailyProtein(600);
        when(dietService.calculateMacros(DietType.BALANCED, 2000)).thenReturn(calculated);

        mockMvc.perform(post("/api/diets/calculate")
                .param("type", "BALANCED")
                .param("targetCalories", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyCalories").value(2000.0));
    }
}
