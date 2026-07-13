package com.eazyplan.repository;

import com.eazyplan.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para {@link User}.
 *
 * <p>Sustituye al par {@code UserRepository} (interfaz) + {@code UserRepositoryImpl}
 * (EntityManager manual) del legado EclipseLink. Spring Data genera la implementación.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
