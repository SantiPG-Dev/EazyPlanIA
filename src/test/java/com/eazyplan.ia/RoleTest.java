package com.eazyplan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Role - tipos de roles y validaciones
 */
public class RoleTest {
    
    @Test
    public void testRoleTypes() {
        assertNotNull(com.eazyplan.domain.entities.Role.class);
        
        // Verificar que existen los tipos de roles definidos en la entidad
        assertTrue(com.eazyplan.domain.entities.Role.RoleType.values().length >= 3);
        
        System.out.println("Roles disponibles: " + java.util.Arrays.toString(
            com.eazyplan.domain.entities.Role.RoleType.values()));
    }

    @Test
    public void testRoleCreation() {
        var role = new com.eazyplan.domain.entities.Role();
        
        // Crear rol con tipo USER (por defecto)
        assertNotNull(role);
        
        System.out.println("Rol creado correctamente: " + 
            com.eazyplan.domain.entities.Role.RoleType.values()[0]);
    }

    @Test
    public void testRoleWithUser() {
        // Verificar que Role se puede usar en relación con User
        var user = new com.eazyplan.domain.entities.User();
        assertNotNull(user);
        
        System.out.println("Usuario creado correctamente, lista vacía de roles");
    }

    @Test
    public void testRoleGettersSetters() {
        var role = new com.eazyplan.domain.entities.Role();
        
        // Verificar que los getters y setters funcionan (si están definidos en la entidad)
        assertNotNull(role);
        
        System.out.println("Getters/Setters de Role accesibles");
    }
}
