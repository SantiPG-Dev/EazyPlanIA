# Design: Fix and Complete EazyPlanIA

## Technical Approach

Fix data layer (transactions, EM lifecycle, broken queries), build scene navigation, wire controllers to services, and clean up tests. All changes stay within existing layered architecture. No new dependencies.

## Architecture Decisions

### EM-per-operation transaction pattern

| Option | Tradeoff | Decision |
|--------|----------|----------|
| (a) Inject EM into repo constructors | Requires DI or factory wiring everywhere; over-engineered for desktop app | Rejected |
| (b) Create EM per operation from shared factory | Simple, stateless repos, each write call gets its own transaction | **Chosen** |

**Rationale**: App is single-user desktop. Per-operation EM creation from `DatabaseConfig.getEntityManagerFactory().createEntityManager()` is the simplest fix. Each `save()`/`delete()` wraps in explicit `begin/commit/rollback`. Read-only methods (find, findAll) skip transactions. All 9 repos get the same treatment — copy-paste pattern, no abstraction layer needed.

### SceneManager as static utility

| Option | Tradeoff | Decision |
|--------|----------|----------|
| (a) Pass Stage through constructor injection | FXML controllers created by FXMLLoader, hard to inject | Rejected |
| (b) Static holder with `init(Stage)` + `switchScene(fxmlPath)` | Simple, JavaFX convention for small apps | **Chosen** |

**Rationale**: Controllers call `SceneManager.switchScene("/presentation/views/DashboardView.fxml")`. EazyPlanApp calls `SceneManager.init(primaryStage)` once at startup. No framework needed.

### Controller wiring — direct service instantiation

| Option | Tradeoff | Decision |
|--------|----------|----------|
| (a) ServiceLocator pattern | Tests referenced it, but adds indirection for no benefit | Rejected |
| (b) Controllers instantiate services directly | Services are simple classes, no DI needed | **Chosen** |

**Rationale**: `new UserService()` in controller fields. Services already hold repo references via `RepoImpl.get()`. No ServiceLocator class needed. Tests also use direct instantiation.

### User exists-queries fix — COUNT queries

| Option | Tradeoff | Decision |
|--------|----------|----------|
| (a) Change to `SELECT COUNT(u) ...` returning Long | Clean, DB-efficient | **Chosen** |
| (b) Use `getResultList().isEmpty()` on existing query | Returns full entity row for a boolean check — wasteful | Rejected |

**Rationale**: Queries currently return `User` but repo casts to `Boolean` — this crashes. Change to `SELECT COUNT(u.id) FROM User u WHERE u.username = :username`, repo returns `query.getSingleResult() > 0`.

### Test cleanup strategy

| Option | Tradeoff | Decision |
|--------|----------|----------|
| (a) Delete `com.eazyplan` test package entirely | 4 files duplicate `com.eazyplan.ia`, all broken | **Chosen** |
| (b) Fix both packages | Unnecessary duplication | Rejected |

**Rationale**: Keep `com.eazyplan.ia` package. Fix tests to use direct service instantiation (not ServiceLocator). Fix `DatabaseConfigTest` — it references `Role.RoleType` which doesn't exist (Role is a simple enum). Add `test-persistence.xml` with H2 in-memory for isolation. Fix `UserServiceTest.testMultipleUsers` — compares boolean to int with `assertEquals(3, ...)`.

## Data Flow

```
LoginView.fxml
  └─ LoginController
       ├─ handleLogin() → UserService.login() → UserRepositoryImpl (EM per-op)
       └─ handleRegister() → UserService.register() → UserRepositoryImpl
            └─ on success → SceneManager.switchScene("/presentation/views/DashboardView.fxml")

SceneManager
  ├─ holds Stage reference (static)
  └─ switchScene(path) → FXMLLoader.load(path) → stage.setScene(newScene)

DashboardView.fxml
  └─ DashboardController
       ├─ Nav buttons → SceneManager.switchScene("WorkoutView.fxml" | ...)
       └─ Displays user info from logged-in User (passed via SceneManager)

WorkoutView.fxml
  └─ WorkoutController
       ├─ initialize() → load workouts from WorkoutService
       └─ CRUD buttons → WorkoutService → WorkoutRepositoryImpl
```

Repository transaction pattern (all 9 repos):
```
save(entity) {
    EntityManager em = DatabaseConfig.getEntityManagerFactory().createEntityManager();
    try {
        em.getTransaction().begin();
        entity.id == null ? em.persist(entity) : em.merge(entity);
        em.getTransaction().commit();
    } catch (Exception e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        throw e;
    } finally {
        em.close();
    }
}
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/repositories/*RepositoryImpl.java` (9 files) | Modify | EM per-operation, explicit transactions, remove singleton `instance`/`get()` |
| `domain/entities/User.java` | Modify | Change exists queries to `SELECT COUNT(u.id)` |
| `domain/entities/Diet.java` | Modify | Move `@NamedQuery` from field to class level |
| `domain/entities/Role.java` | No change | Simple enum is correct; remove from persistence.xml only |
| `META-INF/persistence.xml` | Modify | Remove `Role` class entry (it's an enum, not an entity) |
| `domain/services/MacroLogService.java` | Modify | Remove `entry.setUser(null)` — pass user from caller |
| `domain/services/MicroLogService.java` | Modify | Remove `entry.setUser(null)` — pass user from caller |
| `presentation/SceneManager.java` | Create | Static `init(Stage)`, `switchScene(String)`, `setUserData(Object)` |
| `presentation/controllers/LoginController.java` | Modify | Implement handleLogin/handleRegister with UserService |
| `presentation/controllers/WorkoutController.java` | Modify | Implement CRUD with WorkoutService |
| `presentation/controllers/GroceryListController.java` | Modify | Implement CRUD with GroceryListService |
| `presentation/controllers/DashboardController.java` | Create | Nav buttons, display logged-in user |
| `presentation/controllers/DietController.java` | Create | CRUD with DietService |
| `resources/presentation/views/DashboardView.fxml` | Create | Dashboard layout with nav buttons |
| `resources/presentation/views/DietView.fxml` | Create | Diet CRUD form |
| `resources/presentation/views/WorkoutView.fxml` | Modify | Fix whitespace in TableColumn tags |
| `resources/db.properties` | Delete | Dead file, config is in persistence.xml |
| `EazyPlanApp.java` | Modify | Add `SceneManager.init(primaryStage)` in start() |
| `src/test/java/com/eazyplan/**` (4 files) | Delete | Duplicate test package |
| `src/test/java/com/eazyplan.ia/UserServiceTest.java` | Modify | Use direct `new UserService()`, fix `assertEquals(3, ...)` |
| `src/test/java/com/eazyplan.ia/DietServiceTest.java` | Modify | Use direct `new DietService()`, fix constructor refs |
| `src/test/java/com/eazyplan.ia/DatabaseConfigTest.java` | Modify | Remove Role.RoleType ref, fix close test |
| `src/test/resources/META-INF/persistence.xml` | Create | H2 in-memory test PU |

## Interfaces / Contracts

```java
// SceneManager — new static utility
public class SceneManager {
    private static Stage primaryStage;
    private static Object userData;

    public static void init(Stage stage) { primaryStage = stage; }
    public static void switchScene(String fxmlPath) throws IOException { ... }
    public static void setUserData(Object data) { userData = data; }
    public static Object getUserData() { return userData; }
}
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Macro calculation (DietService) | Pure logic, no DB — existing tests work after fixing imports |
| Integration | Repository CRUD | H2 in-memory PU, EM per-op transactions, verify persist/merge/remove |
| Integration | UserService register/login | Full flow through repo with test PU |

TDD per config — RED-GREEN-REFACTOR. Fix existing broken tests first (green baseline), then add new tests for new code.

## Migration / Rollout

No migration required. Each proposal phase is independently revertable via `git revert`. No schema changes — EclipseLink `create-tables` handles DDL.

## Open Questions

- [ ] Should SceneManager pass the logged-in User object between controllers? (Recommended: yes, via `setUserData`)
- [ ] Should WorkoutController/DietController receive userId from SceneManager or prompt for user selection?
