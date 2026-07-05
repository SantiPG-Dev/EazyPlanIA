package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Workout;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class WorkoutRepositoryImpl implements WorkoutRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<Workout> findAllByUser(Long userId) {
        TypedQuery<Workout> query = entityManager.createNamedQuery("workout.findAllByUser", Workout.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<Workout> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(Workout.class, id))
                .filter(w -> w != null)
                .toList();
    }

    public Workout findById(Long id) {
        return Optional.ofNullable(entityManager.find(Workout.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + id));
    }

    public void save(Workout workout) {
        if (workout.getId() == null) {
            entityManager.persist(workout);
        } else {
            entityManager.merge(workout);
        }
    }

    public void delete(Workout workout) {
        Workout existing = findById(workout.getId());
        entityManager.remove(existing);
    }

    private static final WorkoutRepositoryImpl instance = new WorkoutRepositoryImpl();
    public static WorkoutRepository get() { return instance; }
}
