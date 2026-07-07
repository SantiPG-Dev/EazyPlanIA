# Proposal: fix-and-complete-eazyplan

## Intent

EazyPlanIA cannot run: no transactions in repos, empty controllers, no navigation, broken tests. This change fixes all blockers and builds minimal end-to-end flow (login → dashboard → workout/grocery/diet).

## Scope

### In Scope
- **Phase 1**: Transaction mgmt in 8 repos, fix `@NamedQuery` in Diet, User exists-queries, remove Role from persistence.xml, fix null-user in MacroLog/MicroLogService
- **Phase 2**: Create ServiceLocator, fix all test constructor/method refs, consolidate duplicate test packages, delete dead db.properties
- **Phase 3**: Implement LoginController, create SceneNavigator, dashboard shell with nav, wire EazyPlanApp
- **Phase 4**: Implement WorkoutController, GroceryListController, create DietView+DietController, fix WorkoutView.fxml

### Out of Scope
- MacroLog/MicroLog views, dashboard charts, H2 file migration, DI framework, CSS polish

## Capabilities

### New
- `navigation`: Centralized JavaFX scene switching
- `login-flow`: Auth/register UI wired to UserService
- `dashboard`: Landing view with nav links

### Modified
None — no existing specs.

## Approach

Phases 1+2 → green tests + stable data. Phase 3 → runnable app. Phase 4 → feature CRUD. Transaction fix via `begin/commit/rollback` per save/delete, EM per-operation. Chained PRs: ~150/200/300/250 LOC per phase.

## Affected Areas

| Area | Impact | Change |
|------|--------|--------|
| `domain/repositories/*Impl.java` (8 files) | Modified | Transaction wrappers, EM per-op |
| `domain/entities/{Diet,User}.java` | Modified | Fix @NamedQuery + exists-queries |
| `META-INF/persistence.xml` | Modified | Remove Role entity |
| `domain/services/{MacroLog,MicroLog}Service.java` | Modified | Remove setUser(null) |
| `presentation/controllers/{Login,Workout,GroceryList}Controller.java` | Modified | Full implementation |
| `EazyPlanApp.java` | Modified | Integrate SceneNavigator |
| `presentation/{SceneNavigator,DashboardController,DietController}.java` | New | Nav + dashboard + diet CRUD |
| `resources/presentation/views/{DashboardView,DietView}.fxml` | New | Dashboard + diet layouts |
| `WorkoutView.fxml` | Modified | Fix whitespace |
| `src/test/**` (11 files) | Modified | Fix refs, create ServiceLocator, dedup |
| `ServiceLocator.java` | New | Service accessor |
| `db.properties` | Removed | Dead file |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Per-op transactions slow on H2 file | Low | Acceptable for single-user desktop |
| Test state sharing via singleton repos | Med | Test-specific PU or schema drop |
| Scope creep into MacroLog/MicroLog | Med | Explicit non-goals per PR |
| 900 LOC exceeds review budget | High | Chained PRs |

## Rollback Plan

Each phase independently revertable via `git revert`. No migrations or external config changes.

## Dependencies

- Java 21, JavaFX 21, Maven 3.9+. No new external deps.

## Success Criteria

- [ ] `mvn test` passes after Phase 1+2
- [ ] Login → dashboard → feature views navigation works
- [ ] Workout/GroceryList/Diet CRUD functional
- [ ] No dead code or duplicate test packages
