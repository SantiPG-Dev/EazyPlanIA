package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.MicroLog;

import java.time.LocalDate;

/** Cuerpo de {@code POST /api/users/{userId}/micro-logs}. */
public record MicroLogRequest(
        LocalDate date,
        double sodium,
        double potassium,
        double magnesium,
        double calcium,
        double iron,
        double vitaminC
) {
    public MicroLog toEntity() {
        MicroLog log = new MicroLog();
        log.setDate(date);
        log.setSodium(sodium);
        log.setPotassium(potassium);
        log.setMagnesium(magnesium);
        log.setCalcium(calcium);
        log.setIron(iron);
        log.setVitaminC(vitaminC);
        return log;
    }
}
