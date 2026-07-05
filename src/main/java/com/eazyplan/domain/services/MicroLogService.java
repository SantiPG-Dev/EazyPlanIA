package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.MicroLog;
import com.eazyplan.domain.repositories.MicroLogRepository;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import java.time.LocalDate;
import java.util.List;
import com.eazyplan.domain.repositories.MicroLogRepositoryImpl;

public class MicroLogService {
    private final MicroLogRepository microRepo = MicroLogRepositoryImpl.get();

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
