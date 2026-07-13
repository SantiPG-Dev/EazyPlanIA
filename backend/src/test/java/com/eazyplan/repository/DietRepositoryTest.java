package com.eazyplan.repository;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica que los repositorios Spring Data (métodos derivados) funcionan contra H2.
 * Entregable del Paso 2: tests de repositorio verdes.
 */
@DataJpaTest
class DietRepositoryTest {

    @Autowired
    DietRepository dietRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findAllByUserId_returnsUserDietsSortedByStartDateDesc() {
        User user = userRepository.save(new User("dieter", "Dieter", "d@e.com", "hash"));

        dietRepository.save(new Diet(user, "Keto old", DietType.KETO, LocalDate.of(2026, 1, 1), 0, 0, 0, 0, 0));
        dietRepository.save(new Diet(user, "Vegan new", DietType.VEGAN, LocalDate.of(2026, 6, 1), 0, 0, 0, 0, 0));

        List<Diet> result = dietRepository.findAllByUserIdOrderByStartDateDesc(user.getId());

        assertEquals(2, result.size());
        assertEquals("Vegan new", result.get(0).getName()); // más reciente primero
        assertEquals("Keto old", result.get(1).getName());
    }

    @Test
    void findAllByUserId_excludesOtherUsersDiets() {
        User alice = userRepository.save(new User("alice", "Alice", "a@e.com", "hash"));
        User bob = userRepository.save(new User("bob", "Bob", "b@e.com", "hash"));
        dietRepository.save(new Diet(alice, "Alice diet", DietType.BALANCED, LocalDate.of(2026, 1, 1), 0, 0, 0, 0, 0));

        List<Diet> bobsDiets = dietRepository.findAllByUserIdOrderByStartDateDesc(bob.getId());

        assertTrue(bobsDiets.isEmpty());
    }
}
