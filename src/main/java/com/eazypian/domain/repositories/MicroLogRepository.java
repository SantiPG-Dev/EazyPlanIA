package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.MicroLog;

import java.util.List;

public interface MicroLogRepository {
    List<MicroLog> findAllByUser(Long userId);
    List<MicroLog> findByIds(List<Long> ids);
    MicroLog findById(Long id);
    void save(MicroLog microLog);
    void delete(MicroLog microLog);
}
