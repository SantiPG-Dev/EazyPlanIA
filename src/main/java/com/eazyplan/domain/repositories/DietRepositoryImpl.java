package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DietRepositoryImpl implements DietRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<Diet> findAllByUser(Long userId) {
        TypedQuery<Diet> query = entityManager.createNamedQuery("diet.findAllByUser", Diet.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<Diet> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(Diet.class, id))
                .filter(d -> d != null)
                .toList();
    }

    public Diet findById(Long id) {
        return Optional.ofNullable(entityManager.find(Diet.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Diet not found: " + id));
    }

    public void save(Diet diet) {
        if (diet.getId() == null) {
            entityManager.persist(diet);
        } else {
            entityManager.merge(diet);
        }
    }

    public void delete(Diet diet) {
        Diet existing = findById(diet.getId());
        entityManager.remove(existing);
    }

    private static final DietRepositoryImpl instance = new DietRepositoryImpl();
    public static DietRepository get() { return instance; }
}
