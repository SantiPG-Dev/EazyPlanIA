package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.GroceryItem;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GroceryItemRepositoryImpl implements GroceryItemRepository {

    @Override
    public List<GroceryItem> findAllByList(Long listId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<GroceryItem> query = em.createNamedQuery("groceryItem.findAllByList", GroceryItem.class);
            query.setParameter("listId", listId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<GroceryItem> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(GroceryItem.class, id))
                    .filter(g -> g != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public GroceryItem findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(GroceryItem.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Grocery item not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(GroceryItem groceryItem) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (groceryItem.getId() == null) {
                em.persist(groceryItem);
            } else {
                em.merge(groceryItem);
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
    public void delete(GroceryItem groceryItem) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            GroceryItem existing = em.find(GroceryItem.class, groceryItem.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + groceryItem.getId());
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
