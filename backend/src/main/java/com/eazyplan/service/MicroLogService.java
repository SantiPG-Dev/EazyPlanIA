package com.eazyplan.service;

import com.eazyplan.domain.entities.MicroLog;
import com.eazyplan.repository.MicroLogRepository;
import com.eazyplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MicroLogService {

    private final MicroLogRepository microLogRepository;
    private final UserRepository userRepository;

    public MicroLogService(MicroLogRepository microLogRepository, UserRepository userRepository) {
        this.microLogRepository = microLogRepository;
        this.userRepository = userRepository;
    }

    public List<MicroLog> getUserLogs(Long userId) {
        return microLogRepository.findAllByUserIdOrderByDateDesc(userId);
    }

    public MicroLog logMinerals(MicroLog entry, Long userId) {
        if (entry.getUser() == null && userId != null) {
            userRepository.findById(userId).ifPresent(entry::setUser);
        }
        return microLogRepository.save(entry);
    }

    public void deleteLog(Long id) {
        microLogRepository.deleteById(id);
    }
}
