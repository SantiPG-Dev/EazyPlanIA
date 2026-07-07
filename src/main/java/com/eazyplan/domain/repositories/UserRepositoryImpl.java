package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.User;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User findById(Long id) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id))
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createNamedQuery("existsByUsername", Long.class);
            query.setParameter("username", username);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createNamedQuery("existsByEmail", Long.class);
            query.setParameter("email", email);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public User findByUsername(String username) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query = em.createNamedQuery("findByUsername", User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void save(User user) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
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
    public void delete(User user) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            User existing = em.find(User.class, user.getId());
            if (existing == null) throw new IllegalArgumentException("Entity not found: " + user.getId());
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
