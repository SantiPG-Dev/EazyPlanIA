package com.eazyplan.repository;

import com.eazyplan.domain.entities.Diet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Sustituye a DietRepository + DietRepositoryImpl (EclipseLink manual). */
public interface DietRepository extends JpaRepository<Diet, Long> {
    List<Diet> findAllByUserIdOrderByStartDateDesc(Long userId);
}
