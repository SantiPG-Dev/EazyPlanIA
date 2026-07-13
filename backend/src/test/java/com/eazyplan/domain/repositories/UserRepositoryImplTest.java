package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.User;
import com.eazyplan.infrastructure.database.DatabaseConfig;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private static final String TEST_USER = "repo_test_user_" + System.nanoTime();

    @BeforeAll
    static void ensureFactory() {
        DatabaseConfig.getEntityManagerFactory();
    }

    @AfterAll
    static void cleanup() {
        // Clean up test user to avoid cross-test pollution
        var repo = new UserRepositoryImpl();
        try {
            var user = repo.findByUsername(TEST_USER);
            if (user != null) repo.delete(user);
        } catch (Exception ignored) {}
    }

    @Test
    void save_persistsNewUser() {
        var repo = new UserRepositoryImpl();
        User user = new User(TEST_USER, "Repo Test", "repo@test.com", "pass");

        repo.save(user);

        assertNotNull(user.getId(), "Persisted user should have an ID");
        User found = repo.findById(user.getId());
        assertEquals(TEST_USER, found.getUsername());
    }

    @Test
    void save_mergesExistingUser() {
        var repo = new UserRepositoryImpl();
        User user = new User(TEST_USER + "_merge", "Original", "merge@test.com", "pass");
        repo.save(user);
        Long id = user.getId();

        user.setName("Updated");
        repo.save(user);

        User found = repo.findById(id);
        assertEquals("Updated", found.getName());
    }

    @Test
    void delete_removesUser() {
        var repo = new UserRepositoryImpl();
        User user = new User(TEST_USER + "_del", "Delete Me", "del@test.com", "pass");
        repo.save(user);
        Long id = user.getId();

        repo.delete(user);

        assertThrows(IllegalArgumentException.class, () -> repo.findById(id));
    }

    @Test
    void existsByUsername_returnsFalseForUnknown() {
        var repo = new UserRepositoryImpl();
        assertFalse(repo.existsByUsername("nonexistent_user_" + System.nanoTime()));
    }

    @Test
    void existsByUsername_returnsTrueAfterSave() {
        var repo = new UserRepositoryImpl();
        String uname = TEST_USER + "_exists";
        User user = new User(uname, "Exists", "exists@test.com", "pass");
        repo.save(user);

        assertTrue(repo.existsByUsername(uname));
    }

    @Test
    void existsByEmail_returnsTrueAfterSave() {
        var repo = new UserRepositoryImpl();
        String email = "email_exists_" + System.nanoTime() + "@test.com";
        User user = new User(TEST_USER + "_email", "Email", email, "pass");
        repo.save(user);

        assertTrue(repo.existsByEmail(email));
    }
}
