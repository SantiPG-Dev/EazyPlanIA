package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Workout;

import java.util.List;

public interface WorkoutRepository {
    List<Workout> findAllByUser(Long userId);
    List<Workout> findByIds(List<Long> ids);
    Workout findById(Long id);
    void save(Workout workout);
    void delete(Workout workout);
}
