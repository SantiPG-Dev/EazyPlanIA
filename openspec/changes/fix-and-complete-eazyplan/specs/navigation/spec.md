# Navigation Specification

## Purpose

Defines the centralized scene-switching mechanism for JavaFX views. Any controller can request a scene change without holding a reference to the primary stage.

## Requirements

### Requirement: SceneManager Singleton

The system MUST provide a SceneManager class that holds a reference to the primary Stage. EazyPlanApp MUST register the stage with SceneManager during startup.

#### Scenario: Stage registration at startup

- GIVEN the application starts and creates the primary stage
- WHEN EazyPlanApp.start completes
- THEN SceneManager holds the primary stage reference

### Requirement: Scene Switching

SceneManager MUST expose a method to load an FXML file by path and set it as the root scene of the primary stage, replacing the current scene.

#### Scenario: Switch from login to dashboard

- GIVEN SceneManager holds the primary stage and login view is active
- WHEN a controller requests navigation to DashboardView.fxml
- THEN the FXML is loaded, a new Scene is created, and the stage displays the dashboard

#### Scenario: Invalid FXML path

- GIVEN a request to load a nonexistent FXML file
- WHEN SceneManager attempts to load it
- THEN an IOException or RuntimeException propagates to the caller

### Requirement: Controller Access to SceneManager

All controllers MUST be able to call SceneManager.switchScene(String fxmlPath) to trigger navigation.

#### Scenario: Controller triggers navigation

- GIVEN a controller handling a user action
- WHEN the action requires a view change
- THEN the controller calls SceneManager.switchScene with the target FXML path
