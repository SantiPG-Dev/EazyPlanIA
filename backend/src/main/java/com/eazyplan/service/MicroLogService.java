package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.MicroLog;
import com.eazyplan.domain.entities.User;
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

    @Transactional(readOnly = true)
    public List<MicroLog> getUserLogs(Long userId) {
        return microLogRepository.findAllByUserIdOrderByDateDesc(userId);
    }

    public MicroLog logMinerals(Long userId, MicroLog entry) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        entry.setUser(user);
        return microLogRepository.save(entry);
    }

    public void deleteLog(Long id) {
        if (!microLogRepository.existsById(id)) {
            throw new NotFoundException("MicroLog", id);
        }
        microLogRepository.deleteById(id);
    }
}
