package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.GroceryList;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GroceryListRepositoryImpl implements GroceryListRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<GroceryList> findAllByUser(Long userId) {
        TypedQuery<GroceryList> query = entityManager.createNamedQuery("groceryList.findAllByUser", GroceryList.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public List<GroceryList> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(GroceryList.class, id))
                .filter(g -> g != null)
                .toList();
    }

    public GroceryList findById(Long id) {
        return Optional.ofNullable(entityManager.find(GroceryList.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Grocery list not found: " + id));
    }

    public void save(GroceryList groceryList) {
        if (groceryList.getId() == null) {
            entityManager.persist(groceryList);
        } else {
            entityManager.merge(groceryList);
        }
    }

    public void delete(GroceryList groceryList) {
        GroceryList existing = findById(groceryList.getId());
        entityManager.remove(existing);
    }

    private static final GroceryListRepositoryImpl instance = new GroceryListRepositoryImpl();
    public static GroceryListRepository get() { return instance; }
}
