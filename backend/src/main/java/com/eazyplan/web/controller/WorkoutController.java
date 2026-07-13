package com.eazyplan.web.controller;

import com.eazyplan.domain.entities.Workout;
import com.eazyplan.service.WorkoutService;
import com.eazyplan.web.dto.ExerciseRequest;
import com.eazyplan.web.dto.ExerciseResponse;
import com.eazyplan.web.dto.WorkoutRequest;
import com.eazyplan.web.dto.WorkoutResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * API REST de entrenamientos y ejercicios.
 *
 * <pre>
 * GET    /api/users/{userId}/workouts              → 200 [WorkoutResponse]
 * POST   /api/users/{userId}/workouts              → 201 WorkoutResponse
 * GET    /api/workouts/{workoutId}                 → 200 WorkoutResponse
 * POST   /api/workouts/{workoutId}/exercises       → 201 ExerciseResponse
 * PATCH  /api/workouts/{workoutId}/end             → 200 WorkoutResponse
 * PATCH  /api/exercises/{exerciseId}/complete      → 200 ExerciseResponse
 * DELETE /api/workouts/{workoutId}                 → 204
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @GetMapping("/users/{userId}/workouts")
    public List<WorkoutResponse> listUserWorkouts(@PathVariable Long userId) {
        return workoutService.getUserWorkouts(userId).stream()
                .map(WorkoutResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/workouts")
    public ResponseEntity<WorkoutResponse> createWorkout(@PathVariable Long userId,
                                                          @Valid @RequestBody WorkoutRequest request) {
        Workout created = workoutService.createWorkout(request.toEntity(), userId);
        return ResponseEntity
                .created(URI.create("/api/workouts/" + created.getId()))
                .body(WorkoutResponse.from(created));
    }

    @GetMapping("/workouts/{workoutId}")
    public WorkoutResponse getWorkout(@PathVariable Long workoutId) {
        return WorkoutResponse.from(workoutService.getWorkout(workoutId));
    }

    @PostMapping("/workouts/{workoutId}/exercises")
    public ResponseEntity<ExerciseResponse> addExercise(@PathVariable Long workoutId,
                                                         @Valid @RequestBody ExerciseRequest request) {
        var saved = workoutService.addExercise(workoutId, request.toEntity());
        return ResponseEntity
                .created(URI.create("/api/exercises/" + saved.getId()))
                .body(ExerciseResponse.from(saved));
    }

    @PatchMapping("/workouts/{workoutId}/end")
    public WorkoutResponse endWorkout(@PathVariable Long workoutId,
                                      @RequestBody(required = false) Map<String, LocalDateTime> body) {
        LocalDateTime endTime = body != null ? body.get("endTime") : null;
        return WorkoutResponse.from(workoutService.endWorkout(workoutId, endTime));
    }

    @PatchMapping("/exercises/{exerciseId}/complete")
    public ExerciseResponse completeExercise(@PathVariable Long exerciseId) {
        return ExerciseResponse.from(workoutService.completeExercise(exerciseId));
    }

    @DeleteMapping("/workouts/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long workoutId) {
        workoutService.deleteWorkout(workoutId);
        return ResponseEntity.noContent().build();
    }
}
