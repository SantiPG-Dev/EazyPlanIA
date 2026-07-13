package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Exercise;
import com.eazyplan.domain.entities.Workout;

import java.time.LocalDateTime;
import java.util.List;

/** Respuesta pública de un entrenamiento, incluyendo sus ejercicios. */
public record WorkoutResponse(
        Long id,
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String workoutType,
        String notes,
        List<ExerciseResponse> exercises
) {
    public static WorkoutResponse from(Workout w) {
        List<ExerciseResponse> exercises = w.getExercises().stream()
                .map(ExerciseResponse::from)
                .toList();
        return new WorkoutResponse(
                w.getId(),
                w.getUser().getId(),
                w.getStartTime(),
                w.getEndTime(),
                w.getWorkoutType(),
                w.getNotes(),
                exercises);
    }
}
