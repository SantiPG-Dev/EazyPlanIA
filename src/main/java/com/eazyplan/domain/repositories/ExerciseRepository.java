package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Exercise;

import java.util.List;

public interface ExerciseRepository {
    List<Exercise> findAllByWorkout(Long workoutId);
    List<Exercise> findByIds(List<Long> ids);
    Exercise findById(Long id);
    void save(Exercise exercise);
    void delete(Exercise exercise);
}
