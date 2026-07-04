package com.eazyplan.ia.domain.repositories;

import com.eazyplan.ia.domain.entities.User;
import com.eazyplan.ia.infrastructure.database.DatabaseConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final EntityManager entityManager = DatabaseConfig.getEntityManagerFactory().createEntityManager();

    public User findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public boolean existsByUsername(String username) {
        TypedQuery<Boolean> query = entityManager.createNamedQuery("existsByUsername", Boolean.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    public boolean existsByEmail(String email) {
        TypedQuery<Boolean> query = entityManager.createNamedQuery("existsByEmail", Boolean.class);
        query.setParameter("email", email);
        return query.getSingleResult();
    }

    public User findByUsername(String username) {
        TypedQuery<User> query = entityManager.createNamedQuery("findByUsername", User.class);
        query.setParameter("username", username);
        return query.getSingleResult();
    }

    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    public void delete(User user) {
        User existing = userRepositoryImpl.findById(user.getId());
        entityManager.remove(existing);
    }

    private static final UserRepositoryImpl instance = new UserRepositoryImpl();
    public static UserRepository get() { return instance; }
}
