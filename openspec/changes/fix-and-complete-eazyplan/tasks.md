# Tasks: Fix and Complete EazyPlanIA

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 750-950 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (Data layer) → PR 2 (Nav+Login) → PR 3 (Controllers+Cleanup) |
| Delivery strategy | auto-chain |
| Chain strategy | stacked-to-main |

Decision needed before apply: No
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | PR | Notes |
|------|------|----|-------|
| 1 | Fix repos, entities, services, persistence.xml | PR 1 | Base: main |
| 2 | SceneManager, LoginController, DashboardView | PR 2 | Base: main; depends on PR 1 |
| 3 | Controller wiring + test cleanup | PR 3 | Base: main; depends on PR 1+2 |

## Phase 1: Data Layer Fixes (PR 1)

- [x] 1.1 Add EM-per-op `save()` with begin/commit/rollback to all 8 `*RepositoryImpl.java` in `domain/repositories/`
- [x] 1.2 Add EM-per-op `delete()` with transaction wrapping to all 8 repos
- [x] 1.3 Replace read ops to create fresh EM per call, close in finally; remove singleton `instance`/`get()`
- [x] 1.4 Fix `User.java` @NamedQuery → `SELECT COUNT(u.id) ...` (Long); repo uses `> 0`
- [x] 1.5 Fix `Diet.java`: move `@NamedQuery` from field to class level
- [x] 1.6 Remove `Role` from `persistence.xml` (enum, not entity)
- [x] 1.7 Fix `MacroLogService`/`MicroLogService`: remove `setUser(null)`, pass user from caller
- [x] 1.8 Delete `resources/db.properties`; fix `WorkoutView.fxml` TableColumn whitespace
- [x] 1.9 GREEN baseline: fix broken tests against updated APIs (pre-existing test failures documented; new repo test passes)
- [x] 1.10 Integration test: repo save/delete commit/rollback (H2 in-memory via `UserRepositoryImplTest`)

## Phase 2: Navigation + Login (PR 2)

- [ ] 2.1 Create `presentation/SceneManager.java` — static `init(Stage)`, `switchScene(String)`, `setUserData`/`getUserData`
- [ ] 2.2 Add `SceneManager.init(primaryStage)` in `EazyPlanApp.start()`
- [ ] 2.3 Implement `LoginController.handleLogin()` — validate non-empty, `UserService.login()`, error display, nav to dashboard
- [ ] 2.4 Implement `LoginController.handleRegister()` — validate, `UserService.register()`, handle duplicate, nav to dashboard
- [ ] 2.5 Create `DashboardView.fxml` + `DashboardController.java` with nav buttons to Workout/Grocery/Diet views
- [ ] 2.6 Verify `LoginView.fxml` fx:controller points to `LoginController`
- [ ] 2.7 Tests: empty fields → error; invalid creds → error; success → dashboard navigation

## Phase 3: Controller Wiring + Cleanup (PR 3)

- [ ] 3.1 `WorkoutController.initialize()` populates `workoutsTable` from `WorkoutService`; `handleNewWorkout()` refreshes
- [ ] 3.2 `GroceryListController.initialize()` displays lists from `GroceryListService`; `handleNewGrocery()` refreshes
- [ ] 3.3 Create `DietView.fxml` (ListView + create/delete buttons) + `DietController` with `initialize()`, `handleCreateDiet()`, `handleDeleteDiet()`
- [ ] 3.4 Delete `src/test/java/com/eazyplan/` package (4 duplicate files)
- [ ] 3.5 Fix `UserServiceTest`: direct `new UserService()`, fix `assertEquals(3, boolean)` → type-safe
- [ ] 3.6 Fix `DatabaseConfigTest`: remove `Role.RoleType` ref, fix close test
- [ ] 3.7 Fix `DietServiceTest`: direct `new DietService()`, fix constructor refs
- [ ] 3.8 Create `src/test/resources/META-INF/persistence.xml` (H2 in-memory test PU)
- [ ] 3.9 `mvn test` — all tests pass

## Phase 4: Verification

- [ ] 4.1 `mvn test` — zero failures
- [ ] 4.2 Smoke test: app starts → login → dashboard → nav to workout/grocery/diet
