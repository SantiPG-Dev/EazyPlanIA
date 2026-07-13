package com.eazyplan.repository;

import com.eazyplan.domain.entities.MicroLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MicroLogRepository extends JpaRepository<MicroLog, Long> {
    List<MicroLog> findAllByUserIdOrderByDateDesc(Long userId);
}
