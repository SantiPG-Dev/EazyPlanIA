# Dashboard Specification

## Purpose

Defines the main landing view after login, providing navigation entry points to Workout, Grocery List, and Diet features.

## Requirements

### Requirement: Dashboard View Layout

The system MUST provide a DashboardView.fxml and DashboardController. The view MUST display navigation options for Workout, Grocery List, and Diet features.

#### Scenario: Dashboard loads after login

- GIVEN a successful login or registration
- WHEN the scene switches to DashboardView
- THEN the dashboard is displayed with navigation buttons/links for Workout, Grocery List, and Diet

### Requirement: Dashboard Navigation to Features

DashboardController MUST handle navigation requests to WorkoutView, GroceryListView, and DietView via SceneManager.

#### Scenario: Navigate to Workout from dashboard

- GIVEN the dashboard is displayed
- WHEN the user clicks the Workout navigation element
- THEN SceneManager switches to WorkoutView.fxml

#### Scenario: Navigate to Grocery List from dashboard

- GIVEN the dashboard is displayed
- WHEN the user clicks the Grocery List navigation element
- THEN SceneManager switches to GroceryListView.fxml

#### Scenario: Navigate to Diet from dashboard

- GIVEN the dashboard is displayed
- WHEN the user clicks the Diet navigation element
- THEN SceneManager switches to DietView.fxml
