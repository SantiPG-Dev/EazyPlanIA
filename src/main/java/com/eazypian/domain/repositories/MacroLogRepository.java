package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.MacroLog;

import java.util.List;

public interface MacroLogRepository {
    List<MacroLog> findAllByUser(Long userId);
    List<MacroLog> findByIds(List<Long> ids);
    MacroLog findById(Long id);
    void save(MacroLog macroLog);
    void delete(MacroLog macroLog);
}
