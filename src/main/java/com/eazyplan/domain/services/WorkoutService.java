package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.Workout;
import com.eazyplan.domain.repositories.WorkoutRepository;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import java.util.List;
import java.time.LocalDateTime;
import com.eazyplan.domain.repositories.WorkoutRepositoryImpl;
import com.eazyplan.domain.repositories.ExerciseRepositoryImpl;
import com.eazyplan.domain.repositories.ExerciseRepository;

public class WorkoutService {
    private final WorkoutRepository workoutRepo = new WorkoutRepositoryImpl();
    private final ExerciseRepository exerciseRepo = new ExerciseRepositoryImpl();
    
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

    public void completeExercise(com.eazyplan.domain.entities.Exercise exercise) {
        exercise.setCompleted(true);
        exerciseRepo.save(exercise);
    }

    public void endWorkout(Workout workout, LocalDateTime endTime) {
        workout.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        workoutRepo.save(workout);
    }

    public void deleteWorkout(Workout workout) {
        workoutRepo.delete(workout);
    }
}
