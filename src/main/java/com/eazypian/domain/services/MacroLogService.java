package com.eazyplan.ia.domain.services;

import com.eazyplan.ia.domain.entities.MacroLog;
import com.eazyplan.ia.domain.repositories.MacroLogRepository;
import com.eazyplan.ia.infrastructure.database.DatabaseConfig;

import java.time.LocalDate;
import java.util.List;

public class MacroLogService {
    private final MacroLogRepository macroRepo = MacroLogRepository.get();

    public List<MacroLog> getUserLogs(Long userId) {
        return macroRepo.findAllByUser(userId);
    }

    public MacroLog logMacros(MacroLog entry, DietService dietService, Long userId) {
        entry.setUser(null); // will be set via relationship
        entry.setDiet(dietService.calculateMacros(entry.getDiet().getDietType(), 2000, userId));
        macroRepo.save(entry);
        return entry;
    }

    public void deleteLog(MacroLog log) {
        macroRepo.delete(log);
    }
}
