package com.eazyplan.ia.domain.services;

import com.eazyplan.ia.domain.entities.MicroLog;
import com.eazyplan.ia.domain.repositories.MicroLogRepository;
import com.eazyplan.ia.infrastructure.database.DatabaseConfig;

import java.time.LocalDate;
import java.util.List;

public class MicroLogService {
    private final MicroLogRepository microRepo = MicroLogRepository.get();

    public List<MicroLog> getUserLogs(Long userId) {
        return microRepo.findAllByUser(userId);
    }

    public MicroLog logMinerals(MicroLog entry, Long userId) {
        entry.setUser(null); // will be set via relationship
        microRepo.save(entry);
        return entry;
    }

    public void deleteLog(MicroLog log) {
        microRepo.delete(log);
    }
}
