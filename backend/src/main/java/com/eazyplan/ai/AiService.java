package com.eazyplan.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio que consulta LM Studio (API OpenAI-compatible) para generar
 * dietas, rutinas de entrenamiento y listas de la compra con IA.
 */
@Service
public class AiService {

    private final RestClient restClient;
    private final ObjectMapper mapper;
    private final String model;

    public AiService(
            @Value("${eazyplan.ai.base-url:http://localhost:1234}") String baseUrl,
            @Value("${eazyplan.ai.model:ornith-1.0-9b}") String model) {
        this.restClient = RestClient.create(baseUrl);
        this.mapper = new ObjectMapper();
        this.model = model;
    }

    /** Verifica que LM Studio responda. */
    public boolean isAvailable() {
        try {
            var res = restClient.get().uri("/v1/models").retrieve().toEntity(String.class);
            return res.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera una dieta estructurada a partir de los objetivos del usuario.
     *
     * @param goals objetivos: calorías, tipo de dieta, restricciones, etc.
     * @return JSON con la dieta generada (macros + comidas)
     */
    public JsonNode generateDiet(String goals) {
        String system = """
Eres un nutricionista experto. Dados los objetivos del usuario, genera una dieta
estructurada en JSON válido SIN markdown, SOLO el JSON, con este formato exacto:
{
  "name": "nombre de la dieta",
  "dietType": "BALANCED|LOW_CARBS|HIGH_PROTEIN|VEGAN|KETO|CUSTOM",
  "description": "descripción breve",
  "dailyCalories": número,
  "dailyProtein": gramos,
  "dailyCarbs": gramos,
  "dailyFats": gramos,
  "dailyWater": litros,
  "meals": [
    {"name": "Desayuno", "calories": número, "protein": número, "carbs": número, "fats": número,
     "foods": ["alimento1", "alimento2"]},
    {"name": "Comida", ...},
    {"name": "Cena", ...}
  ]
}
Las cantidades deben ser realistas para un adulto sano.
""";
        return chat(system, goals);
    }

    /**
     * Genera una rutina de entrenamiento.
     *
     * @param goals objetivos: tipo de entreno, grupo muscular, frecuencia, nivel
     * @return JSON con la rutina
     */
    public JsonNode generateWorkout(String goals) {
        String system = """
Eres un entrenador personal experto. Dados los objetivos del usuario, genera una
rutina de entrenamiento en JSON válido SIN markdown, SOLO el JSON, con este formato:
{
  "workoutType": "PUSH|PULL|LEGS|CARDIO|FULL_BODY|CUSTOM",
  "notes": "descripción de la rutina",
  "exercises": [
    {"name": "ejercicio", "muscleGroup": "grupo muscular", "sets": número, "reps": número, "weight": peso estimado en kg}
  ]
}
Los pesos deben ser realistas para un principiante (a menos que se indique otro nivel).
""";
        return chat(system, goals);
    }

    /**
     * Genera una lista de la compra a partir de una dieta (sus macros y comidas).
     *
     * @param dietJson JSON de la dieta (del endpoint anterior o manual)
     * @return JSON con la lista de la compra
     */
    public JsonNode generateGroceryList(String dietJson) {
        String system = """
Eres un experto en planificación de compras. Dada una dieta con sus macros y comidas,
genera una lista de la compra en JSON válido SIN markdown, SOLO el JSON:
{
  "items": [
    {"name": "producto", "category": "categoría", "quantity": cantidad, "unit": "kg|g|L|ud|...", "organic": true|false}
  ]
}
Incluye cantidades suficientes para una semana. Los productos deben ser realistas
y corresponder a los alimentos listados en las comidas de la dieta.
""";
        return chat(system, "Dieta a convertir en compra:\n" + dietJson);
    }

    /**
     * Envía un mensaje a LM Studio y devuelve el contenido parseado como JSON.
     */
    private JsonNode chat(String systemPrompt, String userMessage) {
        try {
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            ArrayNode messages = requestBody.putArray("messages");
            messages.addObject().put("role", "system").put("content", systemPrompt);
            messages.addObject().put("role", "user").put("content", userMessage);

            String response = restClient.post()
                    .uri("/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = mapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Limpiar posibles marcadores markdown ```json ... ```
            content = content.replaceAll("(?s)```(?:json)?\\s*", "").trim();

            return mapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al parsear la respuesta de la IA: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el servidor de IA (LM Studio). Asegúrate de que esté corriendo en localhost:1234");
        }
    }
}
