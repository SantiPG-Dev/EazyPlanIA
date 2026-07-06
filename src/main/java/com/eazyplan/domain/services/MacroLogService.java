package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.MacroLog;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.repositories.MacroLogRepository;
import com.eazyplan.domain.repositories.UserRepository;
import com.eazyplan.domain.repositories.MacroLogRepositoryImpl;
import com.eazyplan.domain.repositories.UserRepositoryImpl;

import java.util.List;

public class MacroLogService {
    private final MacroLogRepository macroRepo = new MacroLogRepositoryImpl();
    private final UserRepository userRepo = new UserRepositoryImpl();

    public List<MacroLog> getUserLogs(Long userId) {
        return macroRepo.findAllByUser(userId);
    }

    public MacroLog logMacros(MacroLog entry, DietService dietService, Long userId) {
        entry.setDiet(dietService.calculateMacros(entry.getDiet().getDietType(), 2000f));
        if (entry.getUser() == null && userId != null) {
            User user = userRepo.findById(userId);
            if (user != null) {
                entry.setUser(user);
            }
        }
        macroRepo.save(entry);
        return entry;
    }

    public void deleteLog(MacroLog log) {
        macroRepo.delete(log);
    }
}
