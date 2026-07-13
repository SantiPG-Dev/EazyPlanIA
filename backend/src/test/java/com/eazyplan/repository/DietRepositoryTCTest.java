package com.eazyplan.repository;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de repositorio contra PostgreSQL real (Testcontainers).
 *
 * <p>Garantiza que las queries derivadas y el mapping JPA funcionan en PostgreSQL,
 * no solo en H2. Arranca un contenedor postgres:16-alpine efímero por ejecución.
 * Requiere Docker.
 */
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DietRepositoryTCTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        r.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        r.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired DietRepository dietRepository;
    @Autowired UserRepository userRepository;

    private User saveUser(String username) {
        return userRepository.save(new User(username, username.toUpperCase(), username + "@test.com", "hash"));
    }

    @Test
    void findAllByUserId_ordersByStartDateDesc() {
        User user = saveUser("dieter_tc");
        dietRepository.save(new Diet(user, "Old", DietType.KETO, LocalDate.of(2026, 1, 1), 0, 0, 0, 0, 0));
        dietRepository.save(new Diet(user, "New", DietType.VEGAN, LocalDate.of(2026, 6, 1), 0, 0, 0, 0, 0));

        List<Diet> result = dietRepository.findAllByUserIdOrderByStartDateDesc(user.getId());

        assertEquals(2, result.size());
        assertEquals("New", result.get(0).getName());
        assertEquals("Old", result.get(1).getName());
    }

    @Test
    void findAllByUserId_isolatesByUser() {
        User alice = saveUser("alice_tc");
        User bob = saveUser("bob_tc");
        dietRepository.save(new Diet(alice, "Alice diet", DietType.BALANCED, LocalDate.of(2026, 1, 1), 0, 0, 0, 0, 0));

        List<Diet> bobsDiets = dietRepository.findAllByUserIdOrderByStartDateDesc(bob.getId());

        assertTrue(bobsDiets.isEmpty());
    }

    @Test
    void identityGeneration_worksInPostgreSQL() {
        User user = saveUser("idgen_tc");
        Diet diet = dietRepository.save(
                new Diet(user, "Test", DietType.CUSTOM, LocalDate.of(2026, 7, 13), 100, 10, 20, 30, 1));

        assertNotNull(diet.getId());
        assertTrue(diet.getId() > 0);
    }
}
