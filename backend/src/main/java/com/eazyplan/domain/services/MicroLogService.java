package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.MicroLog;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.repositories.MicroLogRepository;
import com.eazyplan.domain.repositories.UserRepository;
import com.eazyplan.domain.repositories.MicroLogRepositoryImpl;
import com.eazyplan.domain.repositories.UserRepositoryImpl;

import java.util.List;

public class MicroLogService {
    private final MicroLogRepository microRepo = new MicroLogRepositoryImpl();
    private final UserRepository userRepo = new UserRepositoryImpl();

    public List<MicroLog> getUserLogs(Long userId) {
        return microRepo.findAllByUser(userId);
    }

    public MicroLog logMinerals(MicroLog entry, Long userId) {
        if (entry.getUser() == null && userId != null) {
            User user = userRepo.findById(userId);
            if (user != null) {
                entry.setUser(user);
            }
        }
        microRepo.save(entry);
        return entry;
    }

    public void deleteLog(MicroLog log) {
        microRepo.delete(log);
    }
}
