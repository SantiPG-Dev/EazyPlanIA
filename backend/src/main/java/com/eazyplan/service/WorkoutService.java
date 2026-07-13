package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
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

    @Transactional(readOnly = true)
    public List<Workout> getUserWorkouts(Long userId) {
        return workoutRepository.findAllByUserIdOrderByStartTimeDesc(userId);
    }

    @Transactional(readOnly = true)
    public Workout getWorkout(Long workoutId) {
        return workoutRepository.findWithExercisesById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout", workoutId));
    }

    public Workout createWorkout(Workout workout, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        workout.setUser(user);
        if (workout.getWorkoutType() == null) workout.setWorkoutType("CUSTOM");
        if (workout.getStartTime() == null) workout.setStartTime(LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    public Exercise addExercise(Long workoutId, Exercise exercise) {
        Workout workout = getWorkout(workoutId);
        exercise.setWorkout(workout);
        return exerciseRepository.save(exercise);
    }

    public Exercise completeExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NotFoundException("Exercise", exerciseId));
        exercise.setCompleted(true);
        return exerciseRepository.save(exercise);
    }

    public Workout endWorkout(Long workoutId, LocalDateTime endTime) {
        Workout workout = getWorkout(workoutId);
        workout.setEndTime(endTime != null ? endTime : LocalDateTime.now());
        return workoutRepository.save(workout);
    }

    public void deleteWorkout(Long workoutId) {
        getWorkout(workoutId);
        workoutRepository.deleteById(workoutId);
    }
}
