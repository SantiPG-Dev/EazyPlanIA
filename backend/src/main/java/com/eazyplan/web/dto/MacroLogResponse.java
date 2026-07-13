package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.MacroLog;

import java.time.LocalDate;

/** Respuesta pública de un registro de macronutrientes. */
public record MacroLogResponse(
        Long id,
        Long userId,
        Long dietId,
        LocalDate date,
        double calories,
        double protein,
        double carbs,
        double fats
) {
    public static MacroLogResponse from(MacroLog m) {
        return new MacroLogResponse(
                m.getId(),
                m.getUser().getId(),
                m.getDiet().getId(),
                m.getDate(),
                m.getCalories(),
                m.getProtein(),
                m.getCarbs(),
                m.getFats());
    }
}
