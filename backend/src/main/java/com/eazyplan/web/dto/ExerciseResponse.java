package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Exercise;

/** Respuesta pública de un ejercicio. */
public record ExerciseResponse(
        Long id,
        Long workoutId,
        String name,
        String muscleGroup,
        int sets,
        int reps,
        double weight,
        boolean completed
) {
    public static ExerciseResponse from(Exercise e) {
        return new ExerciseResponse(
                e.getId(),
                e.getWorkout().getId(),
                e.getName(),
                e.getMuscleGroup(),
                e.getSets(),
                e.getReps(),
                e.getWeight(),
                e.isCompleted());
    }
}
