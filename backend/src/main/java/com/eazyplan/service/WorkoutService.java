package com.eazyplan.service;

import com.eazyplan.domain.entities.Exercise;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.entities.Workout;
import com.eazyplan.repository.ExerciseRepository;
import com.eazyplan.repository.UserRepository;
import com.eazyplan.repository.WorkoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutRepository workoutRepository, ExerciseRepository exerciseRepository,
                          UserRepository userRepository) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    public List<Workout> getUserWorkouts(Long userId) {
        return workoutRepository.findAllByUserIdOrderByStartTimeDesc(userId);
    }

    public Workout createWorkout(Workout workout, Long userId, String type, LocalDateTime startTime) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        workout.setUser(user);
        workout.setWorkoutType(type != null ? type : "Custom");
        workout.setStartTime(startTime != null ? startTime : LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    public Exercise completeExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));
        exercise.setCompleted(true);
        return exerciseRepository.save(exercise);
    }

    public Workout endWorkout(Long workoutId, LocalDateTime endTime) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + workoutId));
        workout.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    public void deleteWorkout(Long workoutId) {
        workoutRepository.deleteById(workoutId);
    }
}
