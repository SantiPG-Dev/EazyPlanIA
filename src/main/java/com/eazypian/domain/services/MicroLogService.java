package com.eazypian.domain.services;

import com.eazypian.domain.entities.MicroLog;
import com.eazypian.domain.repositories.MicroLogRepository;
import com.eazypian.infrastructure.database.DatabaseConfig;

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
