package com.eazyplan;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para UserService - login, registro, búsqueda de usuario
 */
public class UserServiceTest {
    
    @BeforeAll
    public static void setUp() {
        // El DatabaseConfig se inicializa con EntityManagerFactory H2 in-memory
        System.out.println("Setup: DatabaseConfig ya está inicializado");
    }

    @Test
    public void testRegisterNewUser() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        // Registrar un nuevo usuario
        var user = userService.register("testuser", "Juan Pérez", "juan@test.com", "password123");
        
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("Juan Pérez", user.getName());
    }

    @Test
    public void testDuplicateUsername() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        // Primer registro exitoso
        userService.register("dupuser", "Duplicado", "dup@test.com", "pass");
        
        // Segundo registro con mismo username debe fallar
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register("dupuser", "Otro", "otro@test.com", "otherpass");
        });
    }

    @Test
    public void testDuplicateEmail() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        userService.register("emailtest", "Email Test", "same@test.com", "pass");
        
        assertThrows(IllegalArgumentException.class, () -> {
            userService.register("otheruser", "Otro", "same@test.com", "pass2");
        });
    }

    @Test
    public void testLoginSuccess() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        // Registrar usuario primero
        var registeredUser = userService.register("loginuser", "Login User", "login@test.com", "securepass");
        
        // Intentar login con credenciales correctas
        boolean success = userService.login("loginuser", "securepass");
        assertTrue(success);
    }

    @Test
    public void testLoginFailure() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        // Registrar usuario primero
        userService.register("failuser", "Fail User", "fail@test.com", "correctpass");
        
        // Intentar login con contraseña incorrecta
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login("failuser", "wrongpassword");
        });
    }

    @Test
    public void testFindByUsername() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("finduser", "Find Me", "find@test.com", "pass");
        
        assertNotNull(user);
        assertEquals("finduser", user.getUsername());
    }

    @Test
    public void testUpdateUser() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user = userService.register("updateuser", "Original Name", "update@test.com", "pass");
        
        // Actualizar nombre y email
        userService.update(user, "Nuevo Nombre", "nuevo@email.com");
        
        assertEquals("Nuevo Nombre", user.getName());
    }

    @Test
    public void testExistsByUsername() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        assertTrue(userService.existsByUsername("existinguser"));
        assertFalse(userService.existsByUsername("nonexistentuser123456789"));
    }

    @Test
    public void testMultipleUsers() throws Exception {
        com.eazyplan.domain.services.UserService userService = 
            com.eazyplan.domain.services.ServiceLocator.getUserService();
        
        var user1 = userService.register("user1", "Primero", "one@test.com", "p1");
        var user2 = userService.register("user2", "Segundo", "two@test.com", "p2");
        var user3 = userService.register("user3", "Tercero", "three@test.com", "p3");
        
        assertTrue(userService.existsByUsername("user1"));
        assertTrue(userService.existsByUsername("user2"));
        assertTrue(userService.existsByUsername("user3"));
    }
}
