package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.Exercise;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.entities.Workout;
import com.eazyplan.repository.ExerciseRepository;
import com.eazyplan.repository.UserRepository;
import com.eazyplan.repository.WorkoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de {@link WorkoutService} con mocks.
 */
@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock WorkoutRepository workoutRepository;
    @Mock ExerciseRepository exerciseRepository;
    @Mock UserRepository userRepository;
    @InjectMocks WorkoutService workoutService;

    @Test
    void createWorkout_setsDefaults_whenTypeAndStartNull() {
        User user = new User("lifter", "L", "l@e.com", "h");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Workout workout = new Workout();
        when(workoutRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Workout result = workoutService.createWorkout(workout, 1L);

        assertNotNull(result.getWorkoutType());    // default "CUSTOM"
        assertNotNull(result.getStartTime());      // default now
        assertEquals(1L, result.getUser().getId());
    }

    @Test
    void createWorkout_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> workoutService.createWorkout(new Workout(), 99L));
    }

    @Test
    void addExercise_linksExerciseToWorkout() {
        Workout workout = new Workout();
        workout.setId(5L);
        when(workoutRepository.findWithExercisesById(5L)).thenReturn(Optional.of(workout));

        Exercise exercise = new Exercise();
        exercise.setName("Squat");
        when(exerciseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercise result = workoutService.addExercise(5L, exercise);

        assertEquals(5L, result.getWorkout().getId());
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void completeExercise_flipsCompletedAndSaves() {
        Exercise exercise = new Exercise();
        exercise.setCompleted(false);
        when(exerciseRepository.findById(3L)).thenReturn(Optional.of(exercise));
        when(exerciseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Exercise result = workoutService.completeExercise(3L);

        assertTrue(result.isCompleted());
        verify(exerciseRepository).save(exercise);
    }

    @Test
    void endWorkout_setsEndTime() {
        Workout workout = new Workout();
        workout.setId(1L);
        when(workoutRepository.findWithExercisesById(1L)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime endTime = LocalDateTime.of(2026, 7, 13, 18, 0);
        Workout result = workoutService.endWorkout(1L, endTime);

        assertEquals(endTime, result.getEndTime());
    }

    @Test
    void endWorkout_defaultsToNow_whenEndTimeNull() {
        Workout workout = new Workout();
        workout.setId(1L);
        when(workoutRepository.findWithExercisesById(1L)).thenReturn(Optional.of(workout));
        when(workoutRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Workout result = workoutService.endWorkout(1L, null);

        assertNotNull(result.getEndTime());
    }

    @Test
    void deleteWorkout_throwsNotFound_whenMissing() {
        when(workoutRepository.findWithExercisesById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> workoutService.deleteWorkout(99L));
        verify(workoutRepository, never()).deleteById(any());
    }
}
