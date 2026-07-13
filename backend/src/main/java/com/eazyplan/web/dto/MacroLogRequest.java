package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.MacroLog;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/** Cuerpo de {@code POST /api/users/{userId}/macro-logs}. */
public record MacroLogRequest(
        @NotNull Long dietId,
        LocalDate date,
        double calories,
        double protein,
        double carbs,
        double fats
) {
    public MacroLog toEntity() {
        MacroLog log = new MacroLog();
        log.setDate(date);
        log.setCalories(calories);
        log.setProtein(protein);
        log.setCarbs(carbs);
        log.setFats(fats);
        return log;
    }
}
