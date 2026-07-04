package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.MicroLog;
import com.eazypian.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MicroLogRepositoryImpl implements MicroLogRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<MicroLog> findAllByUser(Long userId) {
        TypedQuery<MicroLog> query = entityManager.createNamedQuery("microLog.findAllByUser", MicroLog.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<MicroLog> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(MicroLog.class, id))
                .filter(m -> m != null)
                .toList();
    }

    public MicroLog findById(Long id) {
        return Optional.ofNullable(entityManager.find(MicroLog.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Micro log not found: " + id));
    }

    public void save(MicroLog microLog) {
        if (microLog.getId() == null) {
            entityManager.persist(microLog);
        } else {
            entityManager.merge(microLog);
        }
    }

    public void delete(MicroLog microLog) {
        MicroLog existing = microLogRepositoryImpl.findById(microLog.getId());
        entityManager.remove(existing);
    }

    private static final MicroLogRepositoryImpl instance = new MicroLogRepositoryImpl();
    public static MicroLogRepository get() { return instance; }
}
