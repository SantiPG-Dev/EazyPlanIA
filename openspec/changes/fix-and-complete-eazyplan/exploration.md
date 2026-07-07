## Exploration: fix-and-complete-eazyplan

### Current State

EazyPlanIA is a JavaFX 21 desktop app for personal health management (Diets, Exercise & Grocery List) built with EclipseLink 4.0 JPA over H2. The domain layer (entities, repositories, services) is structurally complete but the app **cannot run** — every data operation crashes, all controllers are empty stubs, and there is zero navigation between views. Tests are completely misaligned with the actual source code.

Architecture: Layered — `domain/entities`, `domain/repositories`, `domain/services`, `infrastructure/database`, `presentation/controllers`, `resources/presentation/views`.

**Source inventory**: 48 Java files (9 entities, 8 repo interfaces, 8 repo impls, 6 services, 3 controllers, 1 app main, 1 DB config), 3 FXML views, 11 test files (two duplicate packages).

### Affected Areas — Critical Runtime Bugs

| # | Issue | Files | Severity | LOC Est. |
|---|-------|-------|----------|----------|
| 1 | **No transaction management in repositories** | All 8 `*RepositoryImpl.java` | BLOCKER | ~120 (add begin/commit/rollback wrapper to every save/delete, ~15 lines each) |
| 2 | **@NamedQuery misplaced in Diet.java** | `domain/entities/Diet.java` | BLOCKER | ~5 (move annotation to class level) |
| 3 | **Controllers are empty stubs** | `presentation/controllers/LoginController.java`, `WorkoutController.java`, `GroceryListController.java` | BLOCKER | ~200-300 combined (add @FXML fields, handler methods, service wiring) |
| 3b | **User NamedQueries return User not Boolean** | `domain/entities/User.java` | BLOCKER | ~10 (rewrite existsByUsername/existsByEmail to use COUNT or change repo return types) |
| 3c | **Role registered as JPA entity but is an enum** | `META-INF/persistence.xml` | HIGH | ~1 (remove Role class entry) |
| 3d | **MacroLogService/MicroLogService null out user** | `MacroLogService.java`, `MicroLogService.java` | HIGH | ~4 (remove `entry.setUser(null)` lines) |

### Affected Areas — EntityManager Lifecycle

| # | Issue | Files | Severity | LOC Est. |
|---|-------|-------|----------|----------|
| 8 | **EM created once per singleton, never closed/refreshed** | All 8 `*RepositoryImpl.java` | HIGH | This is solved by the transaction fix — create EM per-operation or per-transaction rather than as a field |

### Affected Areas — Missing Features

| # | Issue | Files to Create | Severity | LOC Est. |
|---|-------|-----------------|----------|----------|
| 4 | **No navigation/scene switching** | `EazyPlanApp.java` (modify) + new `SceneNavigator.java` | BLOCKER for any multi-view app | ~80 |
| 5 | **No DietView/DietController** | `DietView.fxml`, `DietController.java` | FEATURE GAP | ~200 |
| 6 | **No MacroLog/MicroLog views** | `MacroLogView.fxml`, `MacroLogController.java`, `MicroLogView.fxml`, `MicroLogController.java` | FEATURE GAP | ~400 |
| 7 | **No dashboard** | `DashboardView.fxml`, `DashboardController.java` | FEATURE GAP | ~200 |

### Affected Areas — Broken Tests

| # | Issue | Files | Severity | LOC Est. |
|---|-------|-------|----------|----------|
| 9 | **ServiceLocator class does not exist** | Tests reference `ServiceLocator.getUserService()`, `DietService.get()`, `WorkoutService.get()`, `GroceryListService.get()`, `MacroLogService.get()` — none exist | CRITICAL | ~30 (create ServiceLocator or delete it and fix tests) |
| 10 | **Non-existent constructors/methods in tests** | All test files | CRITICAL | ~150-200 (fix or rewrite) |
| 10a | `Diet` 8-param constructor — actual is 9-param (includes User) | `DietServiceTest.java` (both packages) | | |
| 10b | `MacroLog` 5-param constructor — actual is 3-param (User, Diet, LocalDate) | `MacroLogServiceTest.java` | | |
| 10c | `Workout.setName()` — doesn't exist (has `setWorkoutType()`) | `WorkoutServiceTest.java` (both packages) | | |
| 10d | `GroceryItem(name, price)` — no price field. Actual: `(name, category, quantity, unit)` | `GroceryListServiceTest.java` (com.eazyplan) | | |
| 10e | `GroceryList.setName()` — doesn't exist | `GroceryListServiceTest.java` (com.eazyplan) | | |
| 10f | `GroceryList(name, price)` — no such constructor | `GroceryListServiceTest.java` (com.eazyplan) | | |
| 10g | `new Role()` / `Role.RoleType` — Role is an enum (ADMIN, USER), not instantiable | `RoleTest.java`, `DatabaseConfigTest.java` | | |
| 10h | `MacroLogRepository.findByDietIdAndDateRange()` — static method doesn't exist | `MacroLogServiceTest.java` | | |
| 11 | **Duplicate test packages** | `com.eazyplan` (4 files) vs `com.eazyplan.ia` (7 files) | MEDIUM | ~0 (delete one package, fix the other) |

### Affected Areas — Minor

| # | Issue | Files | Severity | LOC Est. |
|---|-------|-------|----------|----------|
| 12 | Dead `db.properties` file | `resources/db.properties` | LOW | ~0 (delete file) |
| 13 | Whitespace in WorkoutView.fxml tag | `WorkoutView.fxml` line 19 | LOW | ~1 (remove leading space) |

### Dependency Graph

```
Phase 1 (Fix Runtime — BLOCKERS)
├── 1. Add transaction management to all repositories
├── 2. Fix @NamedQuery placement in Diet.java
├── 3b. Fix User NamedQueries return type mismatch
├── 3c. Remove Role from persistence.xml entity list
├── 3d. Fix null-user bug in MacroLogService/MicroLogService
└── 8. Fix EM lifecycle (handled by transaction fix — create EM per transaction)

Phase 2 (Fix Tests — required before any TDD work)
├── 9. Create ServiceLocator or rewrite to direct instantiation
├── 10. Fix all non-existent constructor/method references
└── 11. Consolidate duplicate test packages (keep one, delete other)

Phase 3 (Make App Runnable — minimal UI)
├── 3. Implement LoginController (@FXML fields, handleLogin, handleRegister)
├── 4. Create SceneNavigator + integrate into EazyPlanApp
└── Dashboard shell (even just a label + navigation buttons)

Phase 4 (Complete Feature UI)
├── Implement WorkoutController
├── Implement GroceryListController
├── Create DietView + DietController
├── Create MacroLogView + MacroLogController
└── Create MicroLogView + MicroLogController

Phase 5 (Cleanup)
├── Delete dead db.properties
├── Fix WorkoutView.fxml whitespace
└── Polish any remaining issues
```

### Approaches

#### Approach A: "Fix First, Features After" (Recommended)

Fix all critical bugs (Phase 1+2) to get a stable foundation, then build UI incrementally.

- **Pros**: Tests pass before feature work, clean commit history, each phase independently verifiable
- **Cons**: App doesn't become interactive until Phase 3
- **Effort**: Phase 1 (~140 LOC) + Phase 2 (~200 LOC) + Phase 3 (~280 LOC) = ~620 LOC before app is minimally interactive
- **First slice**: Phases 1+2 only — app is not interactive but all domain logic works and tests pass

#### Approach B: "Get Something on Screen Fast"

Fix only transaction management + controllers in parallel, defer test fixes.

- **Pros**: User sees something working sooner
- **Cons**: Tests still broken, tech debt accumulates, hard to verify correctness
- **Effort**: ~500 LOC to get login flow working, but tests remain red
- **Risk**: High — changes without test safety net

#### Approach C: "Full Rewrite of Data Layer"

Replace singleton repos with proper DI (e.g., create EM per operation) and extract transaction helper.

- **Pros**: Cleaner architecture, solves EM lifecycle properly
- **Cons**: Larger initial change, more risk of regression, delays visible progress
- **Effort**: ~300 LOC refactor + everything in Approach A
- **Risk**: Medium — larger blast radius

### Recommendation

**Approach A** — Fix First, Features After. The domain layer is solid in design; the bugs are mechanical (missing transactions, misplaced annotations, stale EM). Fixing these first gives us a green test suite, which is essential per the project's `tdd: true` config. Then build UI incrementally.

The recommended **first slice** to get the app running end-to-end (login → dashboard) is Phases 1+2+3, approximately **900 lines of change**. This exceeds the 400-line review budget, so chained PRs are recommended:

- **PR #1**: Phase 1 — Repository transaction fix + entity annotation fixes (~150 LOC)
- **PR #2**: Phase 2 — Test alignment (ServiceLocator, constructors, dedup) (~200 LOC)
- **PR #3**: Phase 3 — Login flow + SceneNavigator + Dashboard shell (~300 LOC)

### Risks

1. **Transaction-per-operation pattern may cause performance issues** with H2 file-based DB. Each operation opens/closes a transaction. Mitigation: acceptable for a desktop single-user app; optimize later if needed.
2. **User NamedQueries returning User instead of Boolean** — the `existsByUsername` and `existsByEmail` queries select full User objects, not Boolean. This needs careful rewriting (use COUNT query or check resultList.isEmpty()).
3. **Singleton repositories with per-operation EM** — the singleton pattern with `get()` means all tests share state. With H2 in file mode (`jdbc:h2:./db/eazypian`), data persists between tests. Tests that assume clean state will fail. May need test-specific persistence.xml or schema drop.
4. **Scope creep** — Phases 4-5 (full feature UI) are feature work, not bug fixes. The line between "fix" and "new feature" should be explicit in the proposal.
5. **`existsByEmail` returns `query.getSingleResult()` typed as `Boolean`** — but the query returns a `User` entity. This will throw ClassCastException at runtime. Critical fix needed.

### Ready for Proposal

**Yes.** All issues are confirmed with file-level evidence. The fix order and dependencies are clear. The orchestrator should present the phased approach and first-slice scope to the user for approval before proceeding to proposal.
