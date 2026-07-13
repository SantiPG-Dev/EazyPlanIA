package com.eazyplan.repository;

import com.eazyplan.domain.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findAllByUserIdOrderByStartTimeDesc(Long userId);
}
