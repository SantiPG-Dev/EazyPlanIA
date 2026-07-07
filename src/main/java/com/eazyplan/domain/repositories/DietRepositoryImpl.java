package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DietRepositoryImpl implements DietRepository {

    @Override
    public List<Diet> findAllByUser(Long userId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Diet> query = em.createNamedQuery("diet.findAllByUser", Diet.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Diet> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(Diet.class, id))
                    .filter(d -> d != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public Diet findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Diet.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Diet not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(Diet diet) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (diet.getId() == null) {
                em.persist(diet);
            } else {
                em.merge(diet);
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
    public void delete(Diet diet) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Diet existing = em.find(Diet.class, diet.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + diet.getId());
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
