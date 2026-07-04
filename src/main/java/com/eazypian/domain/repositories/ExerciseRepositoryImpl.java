package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.Exercise;
import com.eazypian.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ExerciseRepositoryImpl implements ExerciseRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<Exercise> findAllByWorkout(Long workoutId) {
        TypedQuery<Exercise> query = entityManager.createNamedQuery("exercise.findAllByWorkout", Exercise.class);
        query.setParameter("workoutId", workoutId);
        return query.getResultList();
    }

    public List<Exercise> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(Exercise.class, id))
                .filter(e -> e != null)
                .toList();
    }

    public Exercise findById(Long id) {
        return Optional.ofNullable(entityManager.find(Exercise.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
    }

    public void save(Exercise exercise) {
        if (exercise.getId() == null) {
            entityManager.persist(exercise);
        } else {
            entityManager.merge(exercise);
        }
    }

    public void delete(Exercise exercise) {
        Exercise existing = exerciseRepositoryImpl.findById(exercise.getId());
        entityManager.remove(existing);
    }

    private static final ExerciseRepositoryImpl instance = new ExerciseRepositoryImpl();
    public static ExerciseRepository get() { return instance; }
}
