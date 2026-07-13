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
 * Tests de integración de la API REST de entrenamientos y ejercicios.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WorkoutControllerTest {

    @Autowired
    MockMvc mockMvc;

    private Long registerUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"lifter_%s\",\"name\":\"Lifter\",\"email\":\"l_%s@test.com\",\"password\":\"secret123\"}"
                        .formatted(System.nanoTime(), System.nanoTime())))
                .andReturn();
        return parseId(result);
    }

    private Long parseId(MvcResult result) throws Exception {
        return Long.parseLong(result.getResponse().getContentAsString()
                .replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    private Long createWorkout(Long userId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/{userId}/workouts", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"workoutType\":\"PUSH\",\"notes\":\"Chest day\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return parseId(result);
    }

    @Test
    void createWorkout_returns201_withDefaults() throws Exception {
        Long userId = registerUser();
        Long workoutId = createWorkout(userId);

        mockMvc.perform(get("/api/workouts/{workoutId}", workoutId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workoutId))
                .andExpect(jsonPath("$.workoutType").value("PUSH"))
                .andExpect(jsonPath("$.startTime").isNotEmpty()) // defaults to now
                .andExpect(jsonPath("$.endTime").isEmpty());
    }

    @Test
    void addExercise_returns201_andListedInWorkout() throws Exception {
        Long userId = registerUser();
        Long workoutId = createWorkout(userId);

        mockMvc.perform(post("/api/workouts/{workoutId}/exercises", workoutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Bench Press\",\"muscleGroup\":\"Chest\",\"sets\":4,\"reps\":8,\"weight\":80.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bench Press"))
                .andExpect(jsonPath("$.completed").value(false));

        mockMvc.perform(get("/api/workouts/{workoutId}", workoutId))
                .andExpect(jsonPath("$.exercises[0].name").value("Bench Press"));
    }

    @Test
    void completeExercise_flipsCompletedFlag() throws Exception {
        Long userId = registerUser();
        Long workoutId = createWorkout(userId);

        MvcResult exResult = mockMvc.perform(post("/api/workouts/{workoutId}/exercises", workoutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Squat\",\"sets\":5,\"reps\":5,\"weight\":100}"))
                .andReturn();
        Long exerciseId = parseId(exResult);

        mockMvc.perform(patch("/api/exercises/{exerciseId}/complete", exerciseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void endWorkout_setsEndTime() throws Exception {
        Long userId = registerUser();
        Long workoutId = createWorkout(userId);

        mockMvc.perform(patch("/api/workouts/{workoutId}/end", workoutId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"endTime\":\"2026-07-13T18:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endTime").value("2026-07-13T18:00:00"));
    }

    @Test
    void deleteWorkout_returns204() throws Exception {
        Long userId = registerUser();
        Long workoutId = createWorkout(userId);

        mockMvc.perform(delete("/api/workouts/{workoutId}", workoutId))
                .andExpect(status().isNoContent());
    }
}
