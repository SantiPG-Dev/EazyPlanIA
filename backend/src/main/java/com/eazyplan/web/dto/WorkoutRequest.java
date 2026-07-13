package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Workout;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/** Cuerpo de {@code POST /api/users/{userId}/workouts}. */
public record WorkoutRequest(
        String workoutType,
        LocalDateTime startTime,
        String notes
) {
    public Workout toEntity() {
        Workout w = new Workout();
        w.setWorkoutType(workoutType);
        w.setStartTime(startTime);
        w.setNotes(notes);
        return w;
    }
}
