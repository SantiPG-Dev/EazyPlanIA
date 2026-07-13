package com.eazyplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de EazyPlanIA.
 *
 * <p>Paso 1 del plan de migración: backend base con Spring Boot levantado y un
 * endpoint funcional {@code POST /api/auth/register} contra H2 en memoria.
 */
@SpringBootApplication
public class EazyPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(EazyPlanApplication.class, args);
    }
}
