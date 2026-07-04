package com.eazypian.domain.services;

import com.eazypian.domain.entities.Workout;
import com.eazypian.domain.repositories.WorkoutRepository;
import com.eazypian.infrastructure.database.DatabaseConfig;

import java.util.List;
import java.time.LocalDateTime;

public class WorkoutService {
    private final WorkoutRepository workoutRepo = WorkoutRepository.get();
    
    public List<Workout> getUserWorkouts(Long userId) {
        return workoutRepo.findAllByUser(userId);
    }

    public Workout createWorkout(Workout workout, String type, LocalDateTime startTime) {
        workout.setWorkoutType(type != null ? type : "Custom");
        if (startTime == null) startTime = LocalDateTime.now();
        workout.setStartTime(startTime);
        workoutRepo.save(workout);
        return workout;
    }

    public void completeExercise(com.eazypian.domain.entities.Exercise exercise) {
        exercise.setCompleted(true);
        workoutRepo.get().save(exercise); // indirect via ExerciseRepository
    }

    public void endWorkout(Workout workout, LocalDateTime endTime) {
        workout.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        workoutRepo.save(workout);
    }

    public void deleteWorkout(Workout workout) {
        workoutRepo.delete(workout);
    }
}
