package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Workout;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class WorkoutRepositoryImpl implements WorkoutRepository {

    @Override
    public List<Workout> findAllByUser(Long userId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Workout> query = em.createNamedQuery("workout.findAllByUser", Workout.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Workout> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(Workout.class, id))
                    .filter(w -> w != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public Workout findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Workout.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Workout workout) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (workout.getId() == null) {
                em.persist(workout);
            } else {
                em.merge(workout);
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
    public void delete(Workout workout) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Workout existing = em.find(Workout.class, workout.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + workout.getId());
            em.remove(existing);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
