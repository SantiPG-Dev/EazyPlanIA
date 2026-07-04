package com.eazypian;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para DietService - CRUD dietas y calculadora de macros
 */
public class DietServiceTest {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("Setup: DatabaseConfig inicializado");
    }

    @Test
    public void testCalculateMacrosBalanced() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.BALANCED, 2000f);
        
        assertNotNull(macros);
        assertEquals(2000f, macros.getDailyCalories(), 0.1);
        assertEquals(600f, macros.getDailyProtein(), 0.1); // 30%
        assertEquals(800f, macros.getDailyCarbs(), 0.1);   // 40%
        assertEquals(600f, macros.getDailyFats(), 0.1);    // 30%
    }

    @Test
    public void testCalculateMacrosLowCarbs() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.LOW_CARBS, 1500f);
        
        assertNotNull(macros);
        assertEquals(1500f, macros.getDailyCalories(), 0.1);
        assertEquals(600f, macros.getDailyProtein(), 0.1); // 40%
        assertEquals(300f, macros.getDailyCarbs(), 0.1);   // 20%
        assertEquals(600f, macros.getDailyFats(), 0.1);    // 40%
    }

    @Test
    public void testCalculateMacrosHighProtein() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.HIGH_PROTEIN, 2500f);
        
        assertNotNull(macros);
        assertEquals(2500f, macros.getDailyCalories(), 0.1);
        assertEquals(1125f, macros.getDailyProtein(), 0.1); // 45%
        assertEquals(625f, macros.getDailyCarbs(), 0.1);    // 25%
        assertEquals(750f, macros.getDailyFats(), 0.1);     // 30%
    }

    @Test
    public void testCalculateMacrosKeto() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.KETO, 1800f);
        
        assertNotNull(macros);
        assertEquals(1800f, macros.getDailyCalories(), 0.1);
        assertEquals(450f, macros.getDailyProtein(), 0.1); // 25%
        assertEquals(90f, macros.getDailyCarbs(), 0.1);    // 5%
        assertEquals(1260f, macros.getDailyFats(), 0.1);   // 70%
    }

    @Test
    public void testCalculateMacrosVegan() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.VEGAN, 2000f);
        
        assertNotNull(macros);
        assertEquals(2000f, macros.getDailyCalories(), 0.1);
        assertEquals(500f, macros.getDailyProtein(), 0.1); // 25%
        assertEquals(900f, macros.getDailyCarbs(), 0.1);   // 45%
        assertEquals(600f, macros.getDailyFats(), 0.1);    // 30%
    }

    @Test
    public void testCalculateMacrosCustom() {
        com.eazypian.domain.services.DietService dietService = DietService.get();
        
        var macros = dietService.calculateMacros(com.eazypian.domain.entities.Diet.DietType.CUSTOM, 2000f);
        
        assertNotNull(macros);
        assertEquals(2000f, macros.getDailyCalories(), 0.1);
    }

    @Test
    public void testCreateDietWithUserService() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        // Registrar usuario primero
        var user = userService.register("dietuser", "Diet User", "diet@test.com", "pass");
        
        // Crear dieta con UserService relationship
        com.eazypian.domain.entities.Diet diet = new com.eazypian.domain.entities.Diet();
        diet.setName("Prueba Dieta");
        
        var created = DietService.get().createDiet(diet, userService, user.getId());
        
        assertNotNull(created);
        assertEquals("Prueba Dieta", created.getName());
    }

    @Test
    public void testUpdateDiet() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        // Registrar usuario y dieta
        var user = userService.register("updatediet", "Updated Diet User", "updated@test.com", "pass");
        
        var diet = new com.eazypian.domain.entities.Diet("Test", 
            com.eazypian.domain.entities.Diet.DietType.BALANCED, java.time.LocalDate.now(), 2000f, 600f, 800f, 600f, 3000f);
        
        DietService.get().createDiet(diet, userService, user.getId());
        
        // Actualizar dieta
        var updated = diet;
        if (updated.getDietType() == null) updated.setDietType(com.eazypian.domain.entities.Diet.DietType.LOW_CARBS);
        
        DietService.get().updateDiet(diet, "Actualizada", null, 
            com.eazypian.domain.entities.Diet.DietType.LOW_CARBS, 1500f, 600f, 300f, 600f, 2000f);
        
        assertEquals("Actualizada", diet.getName());
    }

    @Test
    public void testGetUserDiets() {
        com.eazypian.domain.services.UserService userService = 
            com.eazypian.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("multidiets", "Multi Diets User", "multi@test.com", "pass");
        
        // Crear varias dietas
        for (int i = 0; i < 3; i++) {
            com.eazypian.domain.entities.DietType type = 
                com.eazypian.domain.entities.Diet.DietType.values()[i % com.eazypian.domain.entities.Diet.DietType.values().length];
            var diet = new com.eazypian.domain.entities.Diet("Diet " + (i+1), type, java.time.LocalDate.now(), 2000f, 600f, 800f, 600f, 3000f);
            DietService.get().createDiet(diet, userService, user.getId());
        }
        
        // Verificar que se crearon las dietas
        var diets = DietService.get().getUserDiets(user.getId());
        assertEquals(3, diets.size());
    }
}
