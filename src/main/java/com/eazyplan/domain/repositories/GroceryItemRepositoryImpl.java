package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.GroceryItem;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GroceryItemRepositoryImpl implements GroceryItemRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public List<GroceryItem> findAllByList(Long listId) {
        TypedQuery<GroceryItem> query = entityManager.createNamedQuery("groceryItem.findAllByList", GroceryItem.class);
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    public List<GroceryItem> findByIds(List<Long> ids) {
        return Optional.ofNullable(ids).orElse(List.of()).stream()
                .map(id -> entityManager.find(GroceryItem.class, id))
                .filter(g -> g != null)
                .toList();
    }

    public GroceryItem findById(Long id) {
        return Optional.ofNullable(entityManager.find(GroceryItem.class, id))
                .orElseThrow(() -> new IllegalArgumentException("Grocery item not found: " + id));
    }

    public void save(GroceryItem groceryItem) {
        if (groceryItem.getId() == null) {
            entityManager.persist(groceryItem);
        } else {
            entityManager.merge(groceryItem);
        }
    }

    public void delete(GroceryItem groceryItem) {
        GroceryItem existing = findById(groceryItem.getId());
        entityManager.remove(existing);
    }

    private static final GroceryItemRepositoryImpl instance = new GroceryItemRepositoryImpl();
    public static GroceryItemRepository get() { return instance; }
}
