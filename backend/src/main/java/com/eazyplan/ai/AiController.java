package com.eazyplan.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.eazyplan.service.DietService;
import com.eazyplan.service.GroceryListService;
import com.eazyplan.service.WorkoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para generación de contenido con IA.
 *
 * <pre>
 * POST /api/ai/generate-diet      → dieta generada
 * POST /api/ai/generate-workout   → rutina generada
 * POST /api/ai/generate-grocery   → compra generada a partir de una dieta
 * GET  /api/ai/status             → estado del servidor IA
 * POST /api/ai/accept-diet        → guarda la dieta generada
 * POST /api/ai/accept-workout     → guarda la rutina
 * POST /api/ai/accept-grocery     → guarda la lista de compra
 * </pre>
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final DietService dietService;
    private final WorkoutService workoutService;
    private final GroceryListService groceryListService;
    private final ObjectMapper mapper;

    public AiController(AiService aiService, DietService dietService,
                        WorkoutService workoutService, GroceryListService groceryListService) {
        this.aiService = aiService;
        this.dietService = dietService;
        this.workoutService = workoutService;
        this.groceryListService = groceryListService;
        this.mapper = new ObjectMapper();
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        boolean available = aiService.isAvailable();
        return Map.of(
                "available", available,
                "message", available ? "IA conectada" : "IA no disponible"
        );
    }

    @PostMapping("/generate-diet")
    public JsonNode generateDiet(@RequestBody Map<String, String> body) {
        String goals = body.getOrDefault("goals",
                "Dieta equilibrada de 2000 kcal, rica en proteínas");
        return aiService.generateDiet(goals);
    }

    @PostMapping("/generate-workout")
    public JsonNode generateWorkout(@RequestBody Map<String, String> body) {
        String goals = body.getOrDefault("goals",
                "Rutina de torso semanal para principiante");
        return aiService.generateWorkout(goals);
    }

    @PostMapping("/generate-grocery")
    public JsonNode generateGrocery(@RequestBody Map<String, Object> body) {
        String dietJson = mapper.valueToTree(body.get("diet")).toString();
        return aiService.generateGroceryList(dietJson);
    }

    @PostMapping("/accept-diet")
    public ResponseEntity<?> acceptDiet(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        JsonNode dietJson = mapper.valueToTree(body.get("diet"));

        var diet = new com.eazyplan.domain.entities.Diet();
        diet.setName(dietJson.path("name").asText("Dieta IA"));
        diet.setDietType(com.eazyplan.domain.entities.Diet.DietType.valueOf(
                dietJson.path("dietType").asText("BALANCED")));
        diet.setDailyCalories((float) dietJson.path("dailyCalories").asDouble(2000));
        diet.setDailyProtein((float) dietJson.path("dailyProtein").asDouble(150));
        diet.setDailyCarbs((float) dietJson.path("dailyCarbs").asDouble(200));
        diet.setDailyFats((float) dietJson.path("dailyFats").asDouble(65));
        diet.setDailyWater((float) dietJson.path("dailyWater").asDouble(2));
        diet.setStartDate(java.time.LocalDate.now());
        diet.setDescription(dietJson.path("description").asText(null));

        var saved = dietService.createDiet(diet, userId);
        return ResponseEntity.ok().body(Map.of(
                "id", saved.getId(),
                "message", "Dieta guardada correctamente"
        ));
    }

    @PostMapping("/accept-workout")
    public ResponseEntity<?> acceptWorkout(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        JsonNode workoutJson = mapper.valueToTree(body.get("workout"));

        var workout = new com.eazyplan.domain.entities.Workout();
        workout.setWorkoutType(workoutJson.path("workoutType").asText("CUSTOM"));
        workout.setNotes(workoutJson.path("notes").asText(null));
        workout.setStartTime(java.time.LocalDateTime.now());

        var saved = workoutService.createWorkout(workout, userId);

        // Añadir ejercicios
        var exercises = workoutJson.path("exercises");
        if (exercises.isArray()) {
            for (var ex : exercises) {
                var exercise = new com.eazyplan.domain.entities.Exercise();
                exercise.setName(ex.path("name").asText("Ejercicio"));
                exercise.setMuscleGroup(ex.path("muscleGroup").asText(null));
                exercise.setSets(ex.path("sets").asInt(3));
                exercise.setReps(ex.path("reps").asInt(10));
                exercise.setWeight(ex.path("weight").asDouble(0));
                workoutService.addExercise(saved.getId(), exercise);
            }
        }

        return ResponseEntity.ok().body(Map.of(
                "id", saved.getId(),
                "message", "Rutina guardada correctamente con " + exercises.size() + " ejercicios"
        ));
    }

    @PostMapping("/accept-grocery")
    public ResponseEntity<?> acceptGrocery(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        JsonNode groceryJson = mapper.valueToTree(body.get("grocery"));

        var list = groceryListService.createList(userId);

        var items = groceryJson.path("items");
        if (items.isArray()) {
            for (var item : items) {
                var gi = new com.eazyplan.domain.entities.GroceryItem();
                gi.setName(item.path("name").asText("Producto"));
                gi.setCategory(item.path("category").asText(null));
                gi.setQuantity(item.path("quantity").asInt(1));
                gi.setUnit(item.path("unit").asText("ud"));
                gi.setOrganic(item.path("organic").asBoolean(false));
                groceryListService.addItem(list.getId(), gi);
            }
        }

        return ResponseEntity.ok().body(Map.of(
                "id", list.getId(),
                "message", "Lista de compra guardada con " + items.size() + " productos"
        ));
    }
}
