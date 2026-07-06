# Spec: fix-and-complete-eazyplan

## Overview

8 new capability specs for fixing and completing EazyPlanIA: transactional repos, login flow, scene navigation, dashboard, workout/grocery/diet controllers, and test cleanup.

## Capability Specs

### 1. Repository Pattern (`specs/repository-pattern/`)
- 3 requirements: Transactional Save, Transactional Delete, Per-Operation EntityManager
- 7 scenarios: persist, merge, rollback, delete existing, delete nonexistent, concurrent saves
- **Key**: All 8 repos must wrap save/delete in begin/commit/rollback. No singleton EM field.

### 2. Login Flow (`specs/login-flow/`)
- 3 requirements: FXML field binding, login validation/auth, registration
- 5 scenarios: controller load, successful login, empty creds, invalid creds, duplicate username register
- **Key**: LoginController wires to LoginView.fxml fields, validates, authenticates via UserService, shows errors inline.

### 3. Navigation (`specs/navigation/`)
- 3 requirements: SceneManager singleton, scene switching, controller access
- 4 scenarios: stage registration, scene switch, invalid FXML, controller-triggered navigation
- **Key**: SceneManager utility holds primary Stage, switches scenes by FXML path.

### 4. Dashboard (`specs/dashboard/`)
- 2 requirements: view layout, navigation to features
- 4 scenarios: dashboard loads, navigate to workout/grocery/diet
- **Key**: New DashboardView.fxml + DashboardController with nav links to all features.

### 5. Workout Controller (`specs/workout-controller/`)
- 2 requirements: table population, new workout button
- 3 scenarios: data load, empty state, new workout action
- **Key**: WorkoutController wired to WorkoutView.fxml TableView, populates from WorkoutService.

### 6. Grocery List Controller (`specs/grocery-controller/`)
- 2 requirements: list display, new grocery button
- 3 scenarios: data load, empty state, new grocery action
- **Key**: GroceryListController wired to GroceryListView.fxml, populates from GroceryListService.

### 7. Diet View (`specs/diet-view/`)
- 3 requirements: diet list view, create diet, delete diet
- 5 scenarios: data load, empty state, create, delete selected
- **Key**: New DietView.fxml + DietController for CRUD via DietService.

### 8. Test Cleanup (`specs/test-cleanup/`)
- 3 requirements: remove duplicate package, fix API references, tests pass
- 4 scenarios: single package, ServiceLocator exists, assertions match types, full suite passes
- **Key**: Delete com.eazyplan test package, fix ServiceLocator refs, fix assertion types.

## Coverage Summary

| Domain | Requirements | Scenarios | Happy | Edge | Error |
|--------|-------------|-----------|-------|------|-------|
| repository-pattern | 3 | 7 | 3 | 3 | 1 |
| login-flow | 3 | 5 | 2 | 0 | 3 |
| navigation | 3 | 4 | 2 | 1 | 1 |
| dashboard | 2 | 4 | 1 | 0 | 0 |
| workout-controller | 2 | 3 | 1 | 1 | 0 |
| grocery-controller | 2 | 3 | 1 | 1 | 0 |
| diet-view | 3 | 5 | 2 | 1 | 0 |
| test-cleanup | 3 | 4 | 1 | 0 | 1 |
| **Total** | **21** | **35** | **13** | **7** | **6** |
