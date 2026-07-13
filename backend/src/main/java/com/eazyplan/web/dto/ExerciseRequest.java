package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Exercise;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo de {@code POST /api/workouts/{workoutId}/exercises}. */
public record ExerciseRequest(
        String name,
        String muscleGroup,
        @Min(0) int sets,
        @Min(0) int reps,
        double weight
) {
    public Exercise toEntity() {
        Exercise e = new Exercise();
        e.setName(name);
        e.setMuscleGroup(muscleGroup);
        e.setSets(sets);
        e.setReps(reps);
        e.setWeight(weight);
        return e;
    }
}
