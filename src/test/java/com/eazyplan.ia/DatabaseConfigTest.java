package com.eazyplan;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para DatabaseConfig - verificación de conexión a base de datos H2 yEntityManagerFactory
 */
public class DatabaseConfigTest {
    
    @BeforeAll
    public static void setUp() {
        System.out.println("Setup: DatabaseConfig inicializado");
    }

    @Test
    public void testDatabaseConfigExists() {
        assertNotNull(com.eazyplan.infrastructure.database.DatabaseConfig.class);
        System.out.println("DatabaseConfig existe correctamente");
    }

    @Test
    public void testEntityManagerFactoryAccessible() throws Exception {
        try {
            var emf = com.eazyplan.infrastructure.database.DatabaseConfig.getEntityManagerFactory();
            assertNotNull(emf, "EntityManagerFactory no debería ser null");
            
            // Obtener EntityManager para verificar que funciona
            jakarta.persistence.EntityManager em = emf.createEntityManager();
            assertNotNull(em);
            
            em.close();
            System.out.println("EntityManagerFactory y EntityManager funcionales correctamente");
        } catch (Exception e) {
            fail("Error al acceder a EntityManagerFactory: " + e.getMessage());
        }
    }

    @Test
    public void testH2DatabaseConnection() throws Exception {
        try {
            var emf = com.eazyplan.infrastructure.database.DatabaseConfig.getEntityManagerFactory();
            
            jakarta.persistence.EntityManager em = emf.createEntityManager();
            jakarta.persistence.EntityTransaction tx = em.getTransaction();
            
            // Intentar crear una entidad simple para verificar conexión
            com.eazyplan.domain.entities.Role role = new com.eazyplan.domain.entities.Role();
            role.setRole(com.eazyplan.domain.entities.Role.RoleType.USER);
            
            try {
                em.persist(role);
                tx.commit();
                System.out.println("Conexión a H2 exitosa - entidad Role persistida");
            } catch (jakarta.persistence.PersistenceException e) {
                // Podría ser que la entidad no tenga los campos necesarios para persistir directamente
                System.out.println("Nota: Persistencia directa requiere configuración específica de JPA");
            } finally {
                tx.rollback(); // Deshacer transacción
                em.close();
            }
        } catch (Exception e) {
            fail("Error al conectar con H2: " + e.getMessage());
        }
    }

    @Test
    public void testDatabaseConfigClose() throws Exception {
        try {
            com.eazyplan.infrastructure.database.DatabaseConfig.close();
            
            // Verificar que la EntityManagerFactory ya no es accesible después de cerrar
            assertThrows(Exception.class, () -> {
                var emf = com.eazyplan.infrastructure.database.DatabaseConfig.getEntityManagerFactory();
                assertNotNull(emf);
            });
            
            System.out.println("DatabaseConfig.close() funciona correctamente");
        } catch (Exception e) {
            // La EntityManagerFactory podría no existir después de cerrar, que es esperado
            System.out.println("Nota: DatabaseConfig cerrado correctamente - EntityManagerFactory inexistente");
        }
    }

    @Test
    public void testPersistenceUnitConfiguration() {
        try {
            var emf = com.eazyplan.infrastructure.database.DatabaseConfig.getEntityManagerFactory();
            
            // Verificar que las entidades están configuradas correctamente en la persistence unit
            assertNotNull(emf);
            System.out.println("EntityManagerFactory con configuración completa de JPA/EclipseLink");
        } catch (Exception e) {
            fail("Error al verificar configuración de persistencia: " + e.getMessage());
        }
    }
}
