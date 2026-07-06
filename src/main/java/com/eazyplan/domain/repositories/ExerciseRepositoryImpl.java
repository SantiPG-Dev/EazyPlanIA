package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Exercise;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class ExerciseRepositoryImpl implements ExerciseRepository {

    @Override
    public List<Exercise> findAllByWorkout(Long workoutId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Exercise> query = em.createNamedQuery("exercise.findAllByWorkout", Exercise.class);
            query.setParameter("workoutId", workoutId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exercise> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(Exercise.class, id))
                    .filter(e -> e != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public Exercise findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Exercise.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Exercise exercise) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (exercise.getId() == null) {
                em.persist(exercise);
            } else {
                em.merge(exercise);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Exercise exercise) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Exercise existing = em.find(Exercise.class, exercise.getId());
            if (existing != null) em.remove(existing);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
