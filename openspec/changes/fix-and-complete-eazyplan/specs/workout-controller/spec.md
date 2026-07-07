# Workout Controller Specification

## Purpose

Defines the wiring of WorkoutController to WorkoutView.fxml, populating workout data and handling new workout creation.

## Requirements

### Requirement: Workout Table Population

WorkoutController MUST bind to the `workoutsTable` TableView defined in WorkoutView.fxml and populate it with workout data from WorkoutService for the current user.

#### Scenario: View loads with workout data

- GIVEN the user has existing workouts in the database
- WHEN WorkoutView is loaded and the controller initializes
- THEN the TableView is populated with the user's workouts showing name, type, start time, end time, and exercise count

#### Scenario: No workouts for user

- GIVEN the user has no workouts
- WHEN WorkoutView loads
- THEN the TableView is empty with no errors

### Requirement: New Workout Button

WorkoutController MUST handle the `handleNewWorkout` action from the `newWorkoutButton` in WorkoutView.fxml.

#### Scenario: New workout button clicked

- GIVEN WorkoutView is displayed
- WHEN the user clicks the new workout button
- THEN the system initiates workout creation (via WorkoutService) and refreshes the table
