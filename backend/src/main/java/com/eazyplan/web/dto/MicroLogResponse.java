package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.MicroLog;

import java.time.LocalDate;

/** Respuesta pública de un registro de micronutrientes. */
public record MicroLogResponse(
        Long id,
        Long userId,
        LocalDate date,
        double sodium,
        double potassium,
        double magnesium,
        double calcium,
        double iron,
        double vitaminC
) {
    public static MicroLogResponse from(MicroLog m) {
        return new MicroLogResponse(
                m.getId(),
                m.getUser().getId(),
                m.getDate(),
                m.getSodium(),
                m.getPotassium(),
                m.getMagnesium(),
                m.getCalcium(),
                m.getIron(),
                m.getVitaminC());
    }
}
