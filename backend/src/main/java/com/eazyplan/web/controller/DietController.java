package com.eazyplan.web.controller;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.service.DietService;
import com.eazyplan.web.dto.DietRequest;
import com.eazyplan.web.dto.DietResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * API REST de dietas.
 *
 * <pre>
 * GET    /api/users/{userId}/diets        → 200 [DietResponse]
 * POST   /api/users/{userId}/diets        → 201 DietResponse
 * GET    /api/diets/{dietId}              → 200 DietResponse
 * PUT    /api/diets/{dietId}              → 200 DietResponse
 * DELETE /api/diets/{dietId}              → 204
 * POST   /api/diets/calculate             → 200 DietResponse  (utility: reparto de macros)
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class DietController {

    private final DietService dietService;

    public DietController(DietService dietService) {
        this.dietService = dietService;
    }

    @GetMapping("/users/{userId}/diets")
    public List<DietResponse> listUserDiets(@PathVariable Long userId) {
        return dietService.getUserDiets(userId).stream()
                .map(DietResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/diets")
    public ResponseEntity<DietResponse> createDiet(@PathVariable Long userId,
                                                    @Valid @RequestBody DietRequest request) {
        Diet created = dietService.createDiet(request.toEntity(), userId);
        return ResponseEntity
                .created(URI.create("/api/diets/" + created.getId()))
                .body(DietResponse.from(created));
    }

    @GetMapping("/diets/{dietId}")
    public DietResponse getDiet(@PathVariable Long dietId) {
        return DietResponse.from(dietService.getDiet(dietId));
    }

    @PutMapping("/diets/{dietId}")
    public DietResponse updateDiet(@PathVariable Long dietId,
                                   @Valid @RequestBody DietRequest request) {
        return DietResponse.from(dietService.updateDiet(dietId, request.toEntity()));
    }

    @DeleteMapping("/diets/{dietId}")
    public ResponseEntity<Void> deleteDiet(@PathVariable Long dietId) {
        dietService.deleteDiet(dietId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/diets/calculate")
    public DietResponse calculateMacros(@RequestParam DietType type,
                                        @RequestParam float targetCalories) {
        return DietResponse.from(dietService.calculateMacros(type, targetCalories));
    }
}
