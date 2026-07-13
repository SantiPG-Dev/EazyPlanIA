package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Diet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Cuerpo de {@code POST /api/users/{userId}/diets} y {@code PUT /api/diets/{id}}.
 *
 * <p>En PUT todos los campos son opcionales (merge parcial).
 */
public record DietRequest(
        String name,
        String description,
        String dietType,
        LocalDate startDate,
        LocalDate endDate,
        Float dailyCalories,
        Float dailyProtein,
        Float dailyCarbs,
        Float dailyFats,
        Float dailyWater
) {
    /** Construye una entidad {@link Diet} a partir del DTO de creación. */
    public Diet toEntity() {
        Diet d = new Diet();
        if (name != null) d.setName(name);
        d.setDescription(description);
        if (dietType != null) d.setDietType(Diet.DietType.valueOf(dietType));
        d.setStartDate(startDate);
        d.setEndDate(endDate);
        if (dailyCalories != null) d.setDailyCalories(dailyCalories);
        if (dailyProtein != null) d.setDailyProtein(dailyProtein);
        if (dailyCarbs != null) d.setDailyCarbs(dailyCarbs);
        if (dailyFats != null) d.setDailyFats(dailyFats);
        if (dailyWater != null) d.setDailyWater(dailyWater);
        return d;
    }
}
