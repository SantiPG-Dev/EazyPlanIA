package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.MicroLog;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MicroLogRepositoryImpl implements MicroLogRepository {

    @Override
    public List<MicroLog> findAllByUser(Long userId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<MicroLog> query = em.createNamedQuery("microLog.findAllByUser", MicroLog.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MicroLog> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(MicroLog.class, id))
                    .filter(m -> m != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public MicroLog findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(MicroLog.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Micro log not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(MicroLog microLog) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (microLog.getId() == null) {
                em.persist(microLog);
            } else {
                em.merge(microLog);
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
    public void delete(MicroLog microLog) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            MicroLog existing = em.find(MicroLog.class, microLog.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + microLog.getId());
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
