package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.Diet;

import java.time.LocalDate;

/** Respuesta pública de una dieta. */
public record DietResponse(
        Long id,
        Long userId,
        String name,
        String description,
        String dietType,
        LocalDate startDate,
        LocalDate endDate,
        float dailyCalories,
        float dailyProtein,
        float dailyCarbs,
        float dailyFats,
        float dailyWater
) {
    public static DietResponse from(Diet d) {
        return new DietResponse(
                d.getId(),
                d.getUser() != null ? d.getUser().getId() : null,
                d.getName(),
                d.getDescription(),
                d.getDietType().name(),
                d.getStartDate(),
                d.getEndDate(),
                d.getDailyCalories(),
                d.getDailyProtein(),
                d.getDailyCarbs(),
                d.getDailyFats(),
                d.getDailyWater());
    }
}
