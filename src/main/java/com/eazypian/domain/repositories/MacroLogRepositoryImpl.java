package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.MacroLog;
import com.eazypian.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MacroLogRepositoryImpl implements MacroLogRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<MacroLog> findAllByUser(Long userId) {
        TypedQuery<MacroLog> query = entityManager.createNamedQuery("macroLog.findAllByUser", MacroLog.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<MacroLog> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(MacroLog.class, id))
                .filter(m -> m != null)
                .toList();
    }

    public MacroLog findById(Long id) {
        return Optional.ofNullable(entityManager.find(MacroLog.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Macro log not found: " + id));
    }

    public void save(MacroLog macroLog) {
        if (macroLog.getId() == null) {
            entityManager.persist(macroLog);
        } else {
            entityManager.merge(macroLog);
        }
    }

    public void delete(MacroLog macroLog) {
        MacroLog existing = macroLogRepositoryImpl.findById(macroLog.getId());
        entityManager.remove(existing);
    }

    private static final MacroLogRepositoryImpl instance = new MacroLogRepositoryImpl();
    public static MacroLogRepository get() { return instance; }
}
