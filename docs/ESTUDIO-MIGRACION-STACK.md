# 📋 Estudio de migración: EazyPlanIA → Stack Web Moderno

> Documento de análisis y configuración para transformar EazyPlanIA desde una **app de escritorio JavaFX** a una **aplicación web full-stack** (Spring Boot + React), manteniendo su modelo de dominio y metodología *spec-first*.

**Fecha:** 2026-07-13 · **Autor:** Santiago Pérez Gómez

---

## 1. Resumen ejecutivo

EazyPlanIA es hoy una **aplicación de escritorio monolítica** (JavaFX + EclipseLink + H2 embebida).
La migración propuesta la convierte en una **aplicación web cliente-servidor** con dos procesos desplegables
independientes:

```
        ┌──────────────────────┐         REST / JSON          ┌──────────────────────┐
        │   Frontend (React)   │  ◄────────────────────────►  │  Backend (Spring Boot)│
        │  TS + Vite + pnpm    │     HTTP (localhost:5173     │  Java 21 + JPA/Hibernate│
        │  Vitest + TL         │            → 8080)           │  JUnit5 + Mockito     │
        └──────────────────────┘                              └──────────┬───────────┘
                                                                         │ JDBC
                                                              ┌──────────▼───────────┐
                                                              │   PostgreSQL 16       │
                                                              └──────────────────────┘
                                          Todo orquestado por Docker Compose
```

**Magnitud de la migración:** media-alta. El **modelo de dominio y la lógica de negocio se conservan**,
pero la **capa de presentación se reescribe por completo** (JavaFX → React) y la **capa de infraestructura
cambia radicalmente** (H2 embebida → PostgreSQL servidor, EclipseLink → Hibernate, persistencia manual →
Spring Data JPA). Se añaden capas totalmente nuevas: API REST, seguridad, contenedores y CI.

---

## 2. Inventario del estado actual

### 2.1 Stack actual

| Capa | Tecnología actual | Detalle |
|------|-------------------|---------|
| Presentación | **JavaFX 21 + FXML** | 5 vistas (Login, Dashboard, Diet, Workout, GroceryList) + `SceneManager` |
| Lógica | **Services POJO** | `UserService`, `DietService`, `WorkoutService`, `GroceryListService`, `MacroLogService`, `MicroLogService` |
| Persistencia | **EclipseLink 4.0 (JPA)** manual | `Repository` (interfaz) + `RepositoryImpl`, `EntityManager` por operación |
| BD | **H2 embebida** (fichero `./db/`) | `drop-and-create-tables` |
| Modularidad | **JPMS** (`module-info.java`) | |
| Build | **Maven** | `pom.xml` plano (sin parent) |
| Tests | **JUnit 5** | Solo `UserRepositoryImplTest` (tests de integración contra H2) |
| CI / Docker | ❌ Ninguno | |
| Repo | GitHub `SantiPG-Dev/EazyPlanIA` | ramas `main`, `develop`, `feature/fixFXML` |

### 2.2 Modelo de datos (entidades JPA reutilizables)

8 entidades + 2 enums, ya con anotaciones **Jakarta Persistence** (`jakarta.persistence.*`) — esto es clave:
**Spring Boot 3 usa exactamente la misma API**, por lo que las entidades migran casi sin cambios.

```
User ──┬──< Diet ──< MacroLog
       ├──< Workout ──< Exercise
       ├──< GroceryList ──< GroceryItem
       └──< MicroLog

Enums: Role (ADMIN, USER), Diet.DietType (BALANCED, LOW_CARBS, HIGH_PROTEIN, VEGAN, KETO, CUSTOM)
```

Relaciones `@OneToMany`/`@ManyToOne` con `cascade = ALL` + `orphanRemoval`, generación por `SEQUENCE`.

### 2.3 Patrones actuales relevantes para la migración

- **Repositorio interfaz + implementación** → en Spring Data JPA la implementación la genera el framework;
  se conserva la *intención* (contrato) pero se elimina el *boilerplate* manual de `EntityManager`.
- **Transacciones explícitas por operación** → Spring las gestiona con `@Transactional`.
- **Servicios instancian repos con `new`** → pasan a ser beans gestionados (`@Service` + inyección).
- **`DatabaseConfig` singleton** → reemplazado por *auto-configuración* de Spring Boot.
- **`SceneManager` + controladores JavaFX** → **desaparecen**; la navegación vive en el router de React.

---

## 3. Mapa de transformación (actual → objetivo)

| Capa | Actual | Objetivo | Esfuerzo |
|------|--------|----------|----------|
| Presentación (cliente) | JavaFX + FXML | **React + TS + Vite** | 🔴 Reescritura total (nueva) |
| API | (la UI llama a servicios Java directamente) | **REST + JSON** (`@RestController`) | 🟡 Nueva capa |
| Lógica de negocio | Services POJO | Mismas clases → **`@Service`** con inyección | 🟢 Bajo (anotaciones + refactor DI) |
| Repositorios | Interfaz + Impl manual (EntityManager) | **Spring Data JPA** (`JpaRepository`) | 🟢 Simplificación (menos código) |
| ORM | EclipseLink 4.0 | **Hibernate** (vía Spring Boot) | 🟢 Migración de anotaciones mínima |
| BD | H2 embebida (fichero) | **PostgreSQL 16** (servidor, contenedor) | 🟡 Config + datos |
| Config persistencia | `persistence.xml` | **`application.yml`** | 🟢 Reescritura |
| Inicialización esquema | `drop-and-create` | **Flyway/Liquibase** (migraciones versionadas) | 🟡 Nueva |
| Seguridad | Contraseña en texto plano | **Spring Security + BCrypt + JWT** | 🟡 Nueva (cubre deuda técnica) |
| Entidades | Jakarta JPA (EclipseLink) | Jakarta JPA (Hibernate) | 🟢 Casi idénticas |
| Modularidad | JPMS `module-info.java` | **Eliminar JPMS** (no recomendado con Spring Boot) | 🟢 Borrado |
| Build backend | Maven (plano) | **Maven + Spring Boot parent** | 🟡 Reestructurar `pom.xml` |
| Build frontend | — | **pnpm + Vite** | 🔴 Nueva |
| Tests backend | JUnit 5 (1 suite, sin mocks) | **JUnit 5 + Mockito** (unitarios aislados) | 🟡 Ampliar |
| Tests frontend | — | **Vitest + Testing Library** | 🔴 Nueva |
| Contenedores | — | **Docker + Docker Compose** (app + bd + (opc.) frontend) | 🟡 Nueva |
| CI | — | **GitHub Actions** (`mvn verify`) | 🟢 Nueva, sencilla |
| IDE | IntelliJ | **IntelliJ (backend) + VS Code (frontend)** | 🟢 Config |

> **Nota sobre «Spring Boot Memory»**: en el stack solicitado figura «Spring Boot Memory». Se interpreta como
> **Spring Boot** a secas (posible autocorrección). Si se refiere a **Spring Boot conGraalVM Native Image** o a
> otra variante específica, el enfoque de este estudio es compatible; el *native image* se deja como optimización
> futura (apartado 9.6).

---

## 4. Lo que se reutiliza vs lo que se reescribe

### ✅ Reutilizable (con ajustes menores)
- **8 entidades JPA** — ya usan `jakarta.persistence`. Ajustes: Hibernate es más estricto con `float`/`double`
  (recomendar `BigDecimal` para macros), y `SEQUENCE` funciona en PostgreSQL nativamente.
- **Lógica de negocio** de los servicios (cálculo de macros por tipo de dieta, reglas de `createWorkout`,
  `markPurchased`, etc.) — se traslada a métodos `@Service`.
- **Metodología spec-first** (carpeta `openspec/`) — se enriquece con specs de los nuevos endpoints REST.
- **Modelo de datos y decisiones de diseño** documentadas en el README.

### 🔴 Reescritura / nuevo
- **Todo JavaFX** (controladores, FXML, `SceneManager`, `EazyPlanApp`) → se elimina y se sustituye por React.
- **Capa REST** (controladores + DTOs) → nueva.
- **Frontend completo** → nuevo.
- **Infraestructura de persistencia** (`DatabaseConfig`, `persistence.xml`) → reemplazada por Spring.
- **Docker, CI, tooling frontend** → nuevo.

---

## 5. Arquitectura objetivo

### 5.1 Estructura de proyecto (monorepo)

```
EazyPlanIA/
├── backend/                         # 🔵 Spring Boot
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/eazyplan/
│   │   │   ├── EazyPlanApplication.java        # @SpringBootApplication
│   │   │   ├── domain/
│   │   │   │   ├── entities/                   # User, Diet, Workout, ... (migradas)
│   │   │   │   └── enums/                      # Role, DietType
│   │   │   ├── repository/                     # Spring Data JPA (interfaces)
│   │   │   ├── service/                        # @Service (lógica migrada)
│   │   │   ├── web/
│   │   │   │   ├── controller/                 # @RestController
│   │   │   │   ├── dto/                        # Request/Response DTOs
│   │   │   │   └── advice/                     # @ControllerAdvice (errores)
│   │   │   ├── security/                       # SecurityConfig, JwtFilter, ...
│   │   │   └── config/                         # CorsConfig, etc.
│   │   ├── main/resources/
│   │   │   ├── application.yml
│   │   │   ├── application-dev.yml
│   │   │   └── db/migration/                   # Flyway: V1__init.sql, ...
│   │   └── test/java/com/eazyplan/
│   │       ├── service/                        # Mockito unit tests
│   │       ├── controller/                     # @WebMvcTest
│   │       └── repository/                     # @DataJpaTest (Testcontainers)
│   └── Dockerfile
├── frontend/                        # 🟢 React + TS + Vite
│   ├── package.json
│   ├── pnpm-lock.yaml
│   ├── vite.config.ts
│   ├── tsconfig.json
│   ├── index.html
│   ├── src/
│   │   ├── main.tsx
│   │   ├── App.tsx
│   │   ├── api/                       # cliente HTTP (fetch/axios)
│   │   ├── components/
│   │   ├── pages/                     # Login, Dashboard, Diets, Workouts, Grocery
│   │   ├── hooks/
│   │   ├── context/                   # AuthContext
│   │   └── test/                      # setup.ts + pruebas .test.tsx
│   ├── Dockerfile
│   └── nginx.conf                     # (si se sirve build estático en prod)
├── docker-compose.yml                # orquesta db + backend (+ frontend)
├── .github/workflows/ci.yml          # Maven verify + (opc.) build frontend
└── README.md
```

> Se opta por **monorepo con `backend/` y `frontend/`** porque clarifica la separación de los dos *pipelines*
> (Maven vs pnpm) y porque GitHub Actions puede ejecutar ambos trabajos en el mismo repo.

### 5.2 Flujo de una petición

```
React (fetch /api/diets)
  → Vite dev proxy (:5173 → :8080)
    → JwtAuthFilter (valida token)
      → DietController (@RestController)
        → DietService (@Service, @Transactional)
          → DietRepository (JpaRepository)
            → Hibernate → PostgreSQL
```

---

## 6. Configuración por capas (con código concreto)

### 6.1 Backend — `pom.xml` (Spring Boot)

```xml
<project ...>
  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.4.1</version>            <!-- usa la 3.4.x más reciente; soporta Java 21 -->
    <relativePath/>
  </parent>

  <groupId>com.eazyplan</groupId>
  <artifactId>eazyplan-backend</artifactId>
  <version>1.0-SNAPSHOT</version>

  <properties>
    <java.version>21</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
      <scope>runtime</scope>
    </dependency>

    <!-- Migraciones de esquema -->
    <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
      <groupId>org.flywaydb</groupId>
      <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-api</artifactId>
      <version>0.12.6</version>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-impl</artifactId>
      <version>0.12.6</version>
      <scope>runtime</scope>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-jackson</artifactId>
      <version>0.12.6</version>
      <scope>runtime</scope>
    </dependency>

    <!-- Test -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
    </dependency>
    <!-- H2 solo para tests de repositorio rápidos (opcional, junto a Testcontainers) -->
    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

> **Eliminamos** `module-info.java`: Spring Boot no requiere JPMS y sus starters no exponen módulos
> limpios. El encapsulamiento por paquetes es suficiente para este proyecto.

### 6.2 Backend — `application.yml`

```yaml
spring:
  application:
    name: eazyplan-backend

  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:eazyplan}
    username: ${DB_USER:eazyplan}
    password: ${DB_PASSWORD:eazyplan}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate          # Flyway gestiona el esquema; validate lo comprueba
    properties:
      hibernate:
        format_sql: true
        jdbc.time_zone: UTC
    open-in-view: false           # recomendado: evitar OSIV en APIs REST

  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080

eazyplan:
  security:
    jwt:
      secret: ${JWT_SECRET:cambia-esta-clave-usa-256-bits-en-produccion}
      expiration-minutes: 60

logging:
  level:
    org.hibernate.SQL: warn
```

### 6.3 Punto de entrada

```java
package com.eazyplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EazyPlanApplication {
    public static void main(String[] args) {
        SpringApplication.run(EazyPlanApplication.class, args);
    }
}
```

### 6.4 Migración de entidades (cambios mínimos)

Las entidades se mueven de `eclipselink` a `hibernate` sin tocar las anotaciones (`jakarta.persistence`
es la misma). Ajustes recomendados:

```java
// Ejemplo: Diet.java
// 1) float/double → Double (o BigDecimal para macros). Hibernate acepta primitivas,
//    pero Double permite null y BigDecimal evita errores de redondeo.
@Column(nullable = false)
private Double dailyCalories;   // era float

// 2) Sustituir @NamedQuery por métodos derivados en el repositorio (Spring Data),
//    o conservarlas con @Query. Las @NamedQuery puras de EclipseLink se trasladan
//    a @Query("...") JPQL en la interfaz JpaRepository.
```

> **Enums anidados:** `Diet.DietType` conviene extraerlo a su propio fichero `DietType.java`
> (más limpio para serializar a JSON y para mapear desde el frontend).

### 6.5 Repositorios → Spring Data JPA

**Antes** (78 líneas de `EntityManager` manual en `UserRepositoryImpl`):

```java
public class UserRepositoryImpl implements UserRepository {
    public User findByUsername(String username) {
        EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
        try { /* ... */ } finally { em.close(); }
    }
    // save, delete, existsByUsername, existsByEmail ... todo a mano
}
```

**Después** (Spring Data genera la implementación):

```java
package com.eazyplan.repository;

import com.eazyplan.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

> La interfaz `UserRepository` + la clase `UserRepositoryImpl` **desaparecen**: Spring Data provee
> `findById`, `save`, `delete`, `existsByUsername`, `existsByEmail` y `findByUsername` automáticamente.

### 6.6 Servicios → `@Service` con inyección

```java
package com.eazyplan.service;

import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor (recomendado; spring lo gestiona)
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String name, String email, String rawPassword) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username already exists");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered");

        User user = new User(username, name, email, passwordEncoder.encode(rawPassword)); // ← BCrypt
        return userRepository.save(user);
    }
    // ... la lógica de calculateMacros(), update(), etc. se traslada igual
}
```

> **Deuda técnica resuelta:** las contraseñas dejan de almacenarse en texto plano → **BCrypt**.
> (El README actual ya lo flaggea como mejora pendiente.)

### 6.7 Capa REST — Controladores + DTOs

```java
// DTO de entrada
public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String name,
        @Email String email,
        @NotBlank String password) {}

// DTO de salida (nunca exponer la entidad completa con el password)
public record UserResponse(Long id, String username, String name, String email, String role) {}

// Controlador
package com.eazyplan.web.controller;

import com.eazyplan.service.UserService;
import com.eazyplan.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) { this.userService = userService; }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req.username(), req.name(), req.email(), req.password());
        var body = new UserResponse(user.getId(), user.getUsername(), user.getName(), user.getEmail(), user.getRole().name());
        return ResponseEntity.created(URI.create("/api/users/" + user.getId())).body(body);
    }
}
```

**Mapa de endpoints REST propuesto** (alineado con las entidades existentes):

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro |
| POST | `/api/auth/login` | Login → devuelve JWT |
| GET | `/api/diets` | Dietas del usuario autenticado |
| POST | `/api/diets` | Crear dieta |
| PUT | `/api/diets/{id}` | Actualizar |
| DELETE | `/api/diets/{id}` | Borrar |
| GET | `/api/workouts` | Entrenamientos |
| POST | `/api/workouts/{id}/exercises` | Añadir ejercicio |
| GET | `/api/grocery-lists` | Listas de la compra |
| PATCH | `/api/grocery-lists/{id}/purchase` | Marcar comprada |
| GET/POST | `/api/macro-logs`, `/api/micro-logs` | Seguimiento |

### 6.8 Seguridad — Spring Security + JWT

```java
package com.eazyplan.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) { this.jwtAuthFilter = jwtAuthFilter; }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/error").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

> El `JwtAuthFilter` extrae el `Authorization: Bearer <token>`, lo valida y carga el `Authentication`
> en el `SecurityContextHolder`. El frontend guarda el token tras el login.

### 6.9 Migraciones de esquema — Flyway

`backend/src/main/resources/db/migration/V1__init.sql` (reproducido del modelo JPA):

```sql
CREATE TABLE users (
    id        BIGSERIAL PRIMARY KEY,
    username  VARCHAR(255) NOT NULL UNIQUE,
    name      VARCHAR(255) NOT NULL,
    email     VARCHAR(255),
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(20)  NOT NULL DEFAULT 'USER'
);
CREATE TABLE diets (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users(id),
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(1000),
    diet_type      VARCHAR(30) NOT NULL,
    start_date     DATE NOT NULL,
    end_date       DATE,
    daily_calories DOUBLE PRECISION,
    daily_protein  DOUBLE PRECISION,
    daily_carbs    DOUBLE PRECISION,
    daily_fats     DOUBLE PRECISION,
    daily_water    DOUBLE PRECISION
);
-- ... workouts, exercises, grocery_lists, grocery_items, macro_logs, micro_logs
```

> Esto sustituye el `drop-and-create-tables` actual (que borraba todo en cada arranque).
> `ddl-auto: validate` + Flyway = esquema estable y reproducible, apto para producción.

---

## 7. Frontend — React + TypeScript + Vite

### 7.1 `package.json` (gestionado con pnpm)

```jsonc
{
  "name": "eazyplan-frontend",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "test": "vitest",
    "test:ui": "vitest --ui",
    "lint": "eslint ."
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0",
    "react-router-dom": "^7.0.0"
  },
  "devDependencies": {
    "@testing-library/jest-dom": "^6.6.0",
    "@testing-library/react": "^16.1.0",
    "@testing-library/user-event": "^14.5.0",
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@vitejs/plugin-react": "^4.3.0",
    "eslint": "^9.0.0",
    "jsdom": "^25.0.0",
    "typescript": "^5.7.0",
    "vite": "^6.0.0",
    "vitest": "^2.1.0"
  }
}
```

### 7.2 `vite.config.ts` (con proxy al backend + Vitest)

```ts
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/test/setup.ts',
    css: false,
  },
})
```

### 7.3 Cliente HTTP y contexto de auth (esqueleto)

```ts
// src/api/client.ts
const TOKEN_KEY = 'eazyplan_token'

export function getToken() { return localStorage.getItem(TOKEN_KEY) }
export function setToken(t: string) { localStorage.setItem(TOKEN_KEY, t) }
export function clearToken() { localStorage.removeItem(TOKEN_KEY) }

export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = getToken()
  const res = await fetch(`/api${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init.headers,
    },
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.status === 204 ? (undefined as T) : res.json()
}
```

### 7.4 Equivalencias JavaFX → React

| Vista JavaFX actual | Componente React equivalente |
|---------------------|------------------------------|
| `LoginView.fxml` + `LoginController` | `pages/LoginPage.tsx` |
| `DashboardView.fxml` | `pages/DashboardPage.tsx` |
| `DietView.fxml` | `pages/DietsPage.tsx` |
| `WorkoutView.fxml` | `pages/WorkoutsPage.tsx` |
| `GroceryListView.fxml` | `pages/GroceryPage.tsx` |
| `SceneManager` | `<BrowserRouter>` de `react-router-dom` |

---

## 8. Testing

### 8.1 Backend — JUnit 5 + Mockito

El proyecto actual tiene **un único test de integración** contra H2. La migración introduce dos niveles:

**Unitarios (Mockito, sin BD):** aíslan el `@Service` mockeando el repositorio.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService service;

    @Test
    void register_throws_when_username_exists() {
        when(userRepository.existsByUsername("santi")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
            () -> service.register("santi", "Santi", "x@y.z", "1234"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashes_password_and_persists() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("hashed");

        service.register("santi", "Santi", "x@y.z", "1234");

        verify(userRepository).save(argThat(u -> "hashed".equals(u.getPassword())));
    }
}
```

**De controlador (`@WebMvcTest`)** con `@WithMockUser` y Mockito del servicio.

**De repositorio (`@DataJpaTest`)** con **Testcontainers** (PostgreSQL real en un contenedor efímero),
evitando que los tests pasen en H2 pero fallen en PostgreSQL.

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIT {
    @Container static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");
    // @DynamicPropertySource inyecta la URL del contenedor...
}
```

### 8.2 Frontend — Vitest + Testing Library

```tsx
// src/pages/LoginPage.test.tsx
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'
import { LoginPage } from './LoginPage'

describe('LoginPage', () => {
  it('envía credenciales al pulsar Entrar', async () => {
    const onLogin = vi.fn()
    render(<LoginPage onLogin={onLogin} />)

    await userEvent.type(screen.getByLabelText(/usuario/i), 'santi')
    await userEvent.type(screen.getByLabelText(/contraseña/i), '1234')
    await userEvent.click(screen.getByRole('button', { name: /entrar/i }))

    expect(onLogin).toHaveBeenCalledWith('santi', '1234')
  })
})
```

`src/test/setup.ts`:
```ts
import '@testing-library/jest-dom/vitest'
```

---

## 9. Contenedores y CI

### 9.1 `backend/Dockerfile` (multi-stage)

```dockerfile
# ---- build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 9.2 `frontend/Dockerfile` (build + nginx)

```dockerfile
FROM node:22-alpine AS build
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY . .
RUN pnpm build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 9.3 `docker-compose.yml`

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: eazyplan
      POSTGRES_USER: eazyplan
      POSTGRES_PASSWORD: eazyplan
    ports: ["5432:5432"]
    volumes: ["pgdata:/var/lib/postgresql/data"]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U eazyplan"]
      interval: 5s

  backend:
    build: ./backend
    depends_on:
      db: { condition: service_healthy }
    environment:
      DB_HOST: db
      DB_NAME: eazyplan
      DB_USER: eazyplan
      DB_PASSWORD: eazyplan
      JWT_SECRET: ${JWT_SECRET:-demo-secret-256-bits}
    ports: ["8080:8080"]

  frontend:
    build: ./frontend
    depends_on: [backend]
    ports: ["3000:80"]

volumes:
  pgdata:
```

Arranque completo: `docker compose up --build` → BD + backend + frontend listos.

### 9.4 CI — `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven
      - run: mvn -B verify -f backend/pom.xml

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: pnpm/action-setup@v4
        with: { version: 11 }
      - uses: actions/setup-node@v4
        with:
          node-version: '22'
          cache: pnpm
          cache-dependency-path: frontend/pnpm-lock.yaml
      - run: pnpm install --frozen-lockfile
        working-directory: frontend
      - run: pnpm test -- --run
        working-directory: frontend
      - run: pnpm build
        working-directory: frontend
```

---

## 10. Plan de migración por fases

| Fase | Objetivo | Entregable verificable |
|------|----------|------------------------|
| **0. Scaffolding** | Crear `backend/` y `frontend/`, mover el repo a monorepo, ajustar `.gitignore` | `git status` limpio; estructura creada |
| **1. Backend base** | Spring Boot levantado, `pom.xml`, `application.yml`, `/api/auth/register` con BD en memoria (H2 test) | `mvn spring-boot:run` responde 201 |
| **2. Migrar dominio** | Trasladar 8 entidades + enums, crear `JpaRepository`, migrar `@Service` con DI | Tests de repositorio verdes |
| **3. PostgreSQL + Flyway** | `docker compose up db`, `V1__init.sql`, `ddl-auto: validate` | Backend arranca contra PG real |
| **4. Capa REST completa** | Todos los endpoints de Diet/Workout/Grocery/Macro/Micro + DTOs + `@ControllerAdvice` | Colección de pruebas (Postman) |
| **5. Seguridad** | Spring Security + BCrypt + JWT + `JwtAuthFilter` | Login devuelve token; rutas protegidas |
| **6. Tests backend** | Mockito (services) + `@WebMvcTest` (controllers) + Testcontainers (repo) | `mvn verify` verde en CI |
| **7. Frontend base** | Vite + React + Router + cliente HTTP + `AuthContext` + `LoginPage` | Login funciona contra backend |
| **8. Frontend completo** | Dashboard, Dietas, Entrenamientos, Compra (equivalentes a las 5 vistas FXML) | CRUD funcional en navegador |
| **9. Tests frontend** | Vitest + Testing Library en cada página/componente | `pnpm test` verde |
| **10. Docker + CI** | `docker-compose.yml`, `Dockerfile`s, GitHub Actions | `docker compose up` y CI verde |

**Estrategia de rama:** partir de `develop`, crear `feature/web-migration` y avanzar fase a fase con PRs.

---

## 11. Riesgos y decisiones

| Riesgo / Decisión | Postura |
|-------------------|---------|
| **Pérdida de la UI JavaFX** | Aceptada: es el objetivo. Conservar el repo actual como referencia (rama `legacy/javafx`). |
| **`float`/`double` en macros** | Migrar a `Double`/`BigDecimal`; Hibernate/PostgreSQL manejan mejor tipos nullable. |
| **JPMS (`module-info.java`)** | **Eliminar**: Spring Boot no se beneficia y complica classpath. Encapsulamiento por paquetes. |
| **`drop-and-create`** | **Prohibido en prod**. Flyway con `validate`. Migración de datos opcional de H2 a PG. |
| **Contraseñas en claro** | **Deuda crítica resuelta** en la fase de seguridad (BCrypt). |
| **Sesión de usuario entre vistas** | JavaFX la pasaba por `SceneManager` → en web es **JWT en `AuthContext`**. |
| **Tests lentos por BD** | Testcontainers para integración; Mockito para unitarios (rápidos). |
| **CORS** | El proxy de Vite lo resuelve en dev; en prod, nginx sirve el estático y/o se configura CORS en Spring. |
| **Versiones** | Spring Boot 3.4.x, React 19, Vite 6, Node 22, pnpm 11 — todas presentes en el entorno actual. |

---

## 12. Checklist de configuración final

- [ ] Monorepo `backend/` + `frontend/` creado; `.gitignore` ampliado (`node_modules/`, `dist/`, `.env`)
- [ ] `backend/pom.xml` con Spring Boot parent + starters (web, data-jpa, validation, security) + PG + Flyway + JWT
- [ ] `application.yml` con datasource por variables de entorno y `ddl-auto: validate`
- [ ] 8 entidades migradas a Hibernate (tipos numéricos refinados, `DietType` extraído)
- [ ] 6 interfaces `JpaRepository` (eliminando las `*Impl`)
- [ ] 6 servicios `@Service` con inyección por constructor
- [ ] Controladores `@RestController` + DTOs `record` + `@ControllerAdvice`
- [ ] `SecurityConfig` + BCrypt + `JwtAuthFilter`
- [ ] `db/migration/V1__init.sql` (Flyway)
- [ ] `frontend/` con Vite + React + TS, proxy `/api`, `AuthContext`, 5 páginas
- [ ] Tests: Mockito (`service`), `@WebMvcTest` (`controller`), Testcontainers (`repository`), Vitest (`frontend`)
- [ ] `backend/Dockerfile`, `frontend/Dockerfile`, `docker-compose.yml`
- [ ] `.github/workflows/ci.yml` (backend `mvn verify` + frontend `pnpm test`/`build`)
- [ ] README actualizado al nuevo stack; `legacy/javafx` preservada como referencia

---

## 13. Resumen en una frase

> **El modelo de dominio y la lógica de negocio de EazyPlanIA se conservan casi intactos; lo que cambia
> es el "extremo": el escritorio JavaFX se reemplaza por React, y la persistencia manual EclipseLink+H2
> se sustituye por Spring Data JPA + PostgreSQL, añadiendo API REST, seguridad, contenedores y CI por primera vez.**
