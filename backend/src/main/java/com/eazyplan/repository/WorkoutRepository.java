package com.eazyplan.repository;

import com.eazyplan.domain.entities.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @EntityGraph(attributePaths = "exercises")
    List<Workout> findAllByUserIdOrderByStartTimeDesc(Long userId);

    /** Devuelve el workout con sus ejercicios ya cargados (evita LazyInit con open-in-view=false). */
    @EntityGraph(attributePaths = "exercises")
    Optional<Workout> findWithExercisesById(Long id);
}
