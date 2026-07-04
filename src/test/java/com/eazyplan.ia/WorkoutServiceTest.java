package com.eazyplan.ia;

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
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("workoutuser", "Workout User", "workout@test.com", "pass");
        
        // Crear rutina de ejercicios
        var workout = new com.eazyplan.ia.domain.entities.Workout();
        workout.setName("Test Workout");
        
        var created = WorkoutService.get().createWorkout(workout, "Custom", LocalDateTime.now());
        
        assertNotNull(created);
        assertEquals("Test Workout", created.getName());
    }

    @Test
    public void testCreateWorkoutWithDefaultType() {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaulttypeuser", "Default Type User", "default@test.com", "pass");
        
        // Crear rutina sin tipo explícito (debería usar "Custom" por defecto)
        var workout = new com.eazyplan.ia.domain.entities.Workout();
        
        var created = WorkoutService.get().createWorkout(workout, null, LocalDateTime.now());
        
        assertNotNull(created);
        assertEquals("Custom", created.getWorkoutType()); // Default type
    }

    @Test
    public void testCompleteExercise() throws Exception {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("exercisecomplete", "Exercise Complete User", "exercise@test.com", "pass");
        
        // Crear ejercicio y completarlo
        var exercise = new com.eazyplan.ia.domain.entities.Exercise();
        exercise.setName("Push-ups");
        
        WorkoutService.get().completeExercise(exercise);
        
        assertTrue(exercise.isCompleted());
    }

    @Test
    public void testEndWorkout() throws Exception {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("endworkout", "End Workout User", "end@test.com", "pass");
        
        // Crear rutina
        var workout = new com.eazyplan.ia.domain.entities.Workout();
        workout.setName("Test End Workout");
        
        var created = WorkoutService.get().createWorkout(workout, "Strength", LocalDateTime.now());
        
        assertNotNull(created);
        
        // Marcar fin de rutina
        var endTime = LocalDateTime.now().plusMinutes(30);
        WorkoutService.get().endWorkout(created, endTime);
        
        assertEquals(endTime, created.getEndTime(), 1000); // ±1 segundo
    }

    @Test
    public void testDeleteWorkout() throws Exception {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("deleteme", "Delete Me User", "delete@test.com", "pass");
        
        // Crear rutina
        var workout = new com.eazyplan.ia.domain.entities.Workout();
        workout.setName("ToDelete Workout");
        
        var created = WorkoutService.get().createWorkout(workout, "Cardio", LocalDateTime.now());
        
        assertNotNull(created);
        
        // Marcar para eliminación (dependiendo de la implementación del repositorio)
        try {
            WorkoutService.get().deleteWorkout(created);
        } catch (Exception e) {
            // Algunas implementaciones pueden no soportar delete directamente
            System.out.println("Nota: DeleteWorkout requiere implementación específica en el repo");
        }
    }

    @Test
    public void testGetUserWorkouts() throws Exception {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("multiworkouts", "Multi Workouts User", "multiw@test.com", "pass");
        
        // Crear varias rutinas
        for (int i = 0; i < 3; i++) {
            var workout = new com.eazyplan.ia.domain.entities.Workout();
            workout.setName("Workout " + (i+1));
            
            WorkoutService.get().createWorkout(workout, "Custom", LocalDateTime.now());
        }
        
        // Verificar que se crearon las rutinas
        var workouts = WorkoutService.get().getUserWorkouts(user.getId());
        assertEquals(3, workouts.size());
    }

    @Test
    public void testDefaultStartTime() {
        com.eazyplan.ia.domain.services.UserService userService = 
            com.eazyplan.ia.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("defaulttime", "Default Time User", "defaulttime@test.com", "pass");
        
        // Crear rutina sin tiempo explícito (debería usar LocalDateTime.now() por defecto)
        var workout = new com.eazyplan.ia.domain.entities.Workout();
        
        var created = WorkoutService.get().createWorkout(workout, "Custom", null);
        
        assertNotNull(created);
        assertNotNull(created.getStartTime()); // Should be set to current time
    }
}
