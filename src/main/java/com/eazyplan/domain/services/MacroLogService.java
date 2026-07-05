package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.MacroLog;
import com.eazyplan.domain.repositories.MacroLogRepository;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import java.time.LocalDate;
import java.util.List;
import com.eazyplan.domain.repositories.MacroLogRepositoryImpl;

public class MacroLogService {
    private final MacroLogRepository macroRepo = MacroLogRepositoryImpl.get();

    public List<MacroLog> getUserLogs(Long userId) {
        return macroRepo.findAllByUser(userId);
    }

    public MacroLog logMacros(MacroLog entry, DietService dietService, Long userId) {
        entry.setUser(null); // will be set via relationship
        entry.setDiet(dietService.calculateMacros(entry.getDiet().getDietType(), 2000f));
        macroRepo.save(entry);
        return entry;
    }

    public void deleteLog(MacroLog log) {
        macroRepo.delete(log);
    }
}
