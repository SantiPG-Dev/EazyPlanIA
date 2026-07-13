package com.eazyplan.repository;

import com.eazyplan.domain.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findAllByWorkoutIdOrderBySetsDesc(Long workoutId);
}
