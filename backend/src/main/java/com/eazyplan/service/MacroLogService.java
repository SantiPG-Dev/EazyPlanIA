package com.eazyplan.service;

import com.eazyplan.domain.entities.MacroLog;
import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.MacroLogRepository;
import com.eazyplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MacroLogService {

    private final MacroLogRepository macroLogRepository;
    private final UserRepository userRepository;

    public MacroLogService(MacroLogRepository macroLogRepository, UserRepository userRepository) {
        this.macroLogRepository = macroLogRepository;
        this.userRepository = userRepository;
    }

    public List<MacroLog> getUserLogs(Long userId) {
        return macroLogRepository.findAllByUserIdOrderByDateDesc(userId);
    }

    public MacroLog logMacros(MacroLog entry, Long userId) {
        if (entry.getUser() == null && userId != null) {
            userRepository.findById(userId).ifPresent(entry::setUser);
        }
        return macroLogRepository.save(entry);
    }

    public void deleteLog(Long id) {
        macroLogRepository.deleteById(id);
    }
}
