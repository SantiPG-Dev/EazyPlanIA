package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.GroceryList;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class GroceryListRepositoryImpl implements GroceryListRepository {

    @Override
    public List<GroceryList> findAllByUser(Long userId) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<GroceryList> query = em.createNamedQuery("groceryList.findAllByUser", GroceryList.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<GroceryList> findByIds(List<Long> ids) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(ids).orElse(List.of()).stream()
                    .map(id -> em.find(GroceryList.class, id))
                    .filter(g -> g != null)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public GroceryList findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(GroceryList.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("Grocery list not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public void save(GroceryList groceryList) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (groceryList.getId() == null) {
                em.persist(groceryList);
            } else {
                em.merge(groceryList);
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
    public void delete(GroceryList groceryList) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            GroceryList existing = em.find(GroceryList.class, groceryList.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + groceryList.getId());
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
