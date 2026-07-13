package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.MacroLog;
import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.DietRepository;
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
    private final DietRepository dietRepository;

    public MacroLogService(MacroLogRepository macroLogRepository, UserRepository userRepository,
                           DietRepository dietRepository) {
        this.macroLogRepository = macroLogRepository;
        this.userRepository = userRepository;
        this.dietRepository = dietRepository;
    }

    @Transactional(readOnly = true)
    public List<MacroLog> getUserLogs(Long userId) {
        return macroLogRepository.findAllByUserIdOrderByDateDesc(userId);
    }

    public MacroLog logMacros(Long userId, Long dietId, MacroLog entry) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new NotFoundException("Diet", dietId));
        entry.setUser(user);
        entry.setDiet(diet);
        return macroLogRepository.save(entry);
    }

    public void deleteLog(Long id) {
        if (!macroLogRepository.existsById(id)) {
            throw new NotFoundException("MacroLog", id);
        }
        macroLogRepository.deleteById(id);
    }
}
