package com.eazyplan.repository;

import com.eazyplan.domain.entities.MacroLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MacroLogRepository extends JpaRepository<MacroLog, Long> {
    List<MacroLog> findAllByUserIdOrderByDateDesc(Long userId);
}
