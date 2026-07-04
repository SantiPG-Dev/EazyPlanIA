package com.eazyplan.ia.domain.repositories;

import com.eazyplan.ia.domain.entities.MacroLog;

import java.util.List;

public interface MacroLogRepository {
    List<MacroLog> findAllByUser(Long userId);
    List<MacroLog> findByIds(List<Long> ids);
    MacroLog findById(Long id);
    void save(MacroLog macroLog);
    void delete(MacroLog macroLog);
}
