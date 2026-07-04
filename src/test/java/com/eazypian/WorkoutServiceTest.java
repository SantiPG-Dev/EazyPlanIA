package com.eazypian;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Tests para WorkoutService - CRUD rutinas y ejercicios
 */
public class WorkoutServiceTest {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("Setup: DatabaseConfig inicializado");
    }

    @Test
    public void testCreateWorkout() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("workoutuser", "Workout User", "workout@test.com", "pass");
        
        // Crear rutina de ejercicios
        var workout = new com.eazypian.domain.entities.Workout();
        workout.setName("Test Workout");
        
        var created = WorkoutService.get().createWorkout(workout, "Custom", LocalDateTime.now());
        
        assertNotNull(created);
        assertEquals("Test Workout", created.getName());
    }

    @Test
    public void testCreateWorkoutWithDefaultType() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaulttypeuser", "Default Type User", "default@test.com", "pass");
        
        // Crear rutina sin tipo explícito (debería usar "Custom" por defecto)
        var workout = new com.eazypian.domain.entities.Workout();
        
        var created = WorkoutService.get().createWorkout(workout, null, LocalDateTime.now());
        
        assertNotNull(created);
        assertEquals("Custom", created.getWorkoutType()); // Default type
    }

    @Test
    public void testCompleteExercise() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("exercisecomplete", "Exercise Complete User", "exercise@test.com", "pass");
        
        // Crear ejercicio y completarlo
        var exercise = new com.eazypian.domain.entities.Exercise();
        exercise.setName("Push-ups");
        
        WorkoutService.get().completeExercise(exercise);
        
        assertTrue(exercise.isCompleted());
    }

    @Test
    public void testEndWorkout() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("endworkout", "End Workout User", "end@test.com", "pass");
        
        // Crear rutina
        var workout = new com.eazypian.domain.entities.Workout();
        workout.setName("Test End Workout");
        
        var created = WorkoutService.get().createWorkout(workout, "Strength", LocalDateTime.now());
        
        assertNotNull(created);
        
        // Marcar fin de rutina
        var endTime = LocalDateTime.now().plusMinutes(30);
        WorkoutService.get().endWorkout(created, endTime);
        
        assertEquals(endTime, created.getEndTime(), 1000); // ±1 segundo
    }

    @Test
    public void testGetUserWorkouts() throws Exception {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("multiworkouts", "Multi Workouts User", "multiw@test.com", "pass");
        
        // Crear varias rutinas
        for (int i = 0; i < 3; i++) {
            var workout = new com.eazypian.domain.entities.Workout();
            workout.setName("Workout " + (i+1));
            
            WorkoutService.get().createWorkout(workout, "Custom", LocalDateTime.now());
        }
        
        // Verificar que se crearon las rutinas
        var workouts = WorkoutService.get().getUserWorkouts(user.getId());
        assertEquals(3, workouts.size());
    }

    @Test
    public void testDefaultStartTime() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaulttime", "Default Time User", "defaulttime@test.com", "pass");
        
        // Crear rutina sin tiempo explícito (debería usar LocalDateTime.now() por defecto)
        var workout = new com.eazypian.domain.entities.Workout();
        
        var created = WorkoutService.get().createWorkout(workout, "Custom", null);
        
        assertNotNull(created);
        assertNotNull(created.getStartTime()); // Should be set to current time
    }
}
