package com.eazyplan.ia.domain.repositories;

import com.eazyplan.ia.domain.entities.User;

public interface UserRepository {
    User findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findByUsername(String username);
    void save(User user);
    void delete(User user);
}
