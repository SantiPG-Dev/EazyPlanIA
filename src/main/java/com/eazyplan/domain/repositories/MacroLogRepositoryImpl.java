package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.MacroLog;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MacroLogRepositoryImpl implements MacroLogRepository {

    @Override
    public List<MacroLog> findAllByUser(Long userId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<MacroLog> query = em.createNamedQuery("macroLog.findAllByUser", MacroLog.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MacroLog> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(MacroLog.class, id))
                    .filter(m -> m != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public MacroLog findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(MacroLog.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Macro log not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(MacroLog macroLog) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (macroLog.getId() == null) {
                em.persist(macroLog);
            } else {
                em.merge(macroLog);
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
    public void delete(MacroLog macroLog) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            MacroLog existing = em.find(MacroLog.class, macroLog.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + macroLog.getId());
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
