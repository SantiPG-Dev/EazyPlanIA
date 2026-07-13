package com.eazyplan.infrastructure.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Singleton que gestiona el EntityManagerFactory de EclipseLink/JPA.
 * Inicializa la persistencia una sola vez y reutiliza la factory en toda la app.
 */
public class DatabaseConfig {

    private static final String PERSISTENCE_UNIT = "eazypian";
    private static volatile EntityManagerFactory entityManagerFactory;

    private DatabaseConfig() {}

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return entityManagerFactory;
    }

    /**
     * Cierra la factory. Llamar al shutdown de la app.
     */
    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
