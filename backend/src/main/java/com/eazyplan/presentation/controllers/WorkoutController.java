package com.eazyplan.presentation.controllers;

import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.entities.Workout;
import com.eazyplan.domain.services.WorkoutService;
import com.eazyplan.presentation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDateTime;

public class WorkoutController {

    @FXML private TableView<Workout> workoutsTable;
    @FXML private TableColumn<Workout, String> nameColumn;
    @FXML private TableColumn<Workout, String> typeColumn;
    @FXML private TableColumn<Workout, LocalDateTime> startTimeColumn;
    @FXML private TableColumn<Workout, LocalDateTime> endTimeColumn;
    @FXML private TableColumn<Workout, Number> exercisesCountColumn;
    @FXML private Button newWorkoutButton;

    private final WorkoutService workoutService = new WorkoutService();

    @FXML
    public void initialize() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;

        // ponytail: Workout has no name field; workoutType serves as display name
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("workoutType"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        exercisesCountColumn.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        c.getValue().getExercises().size()));

        loadWorkouts(user);
    }

    private void loadWorkouts(User user) {
        workoutsTable.getItems().setAll(workoutService.getUserWorkouts(user.getId()));
    }

    @FXML
    public void handleNewWorkout() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;
        Workout workout = new Workout(user);
        workoutService.createWorkout(workout, "Custom", null);
        loadWorkouts(user);
    }

    @FXML
    public void goBack() {
        try { SceneManager.switchScene("/presentation/views/DashboardView.fxml"); }
        catch (Exception e) { System.err.println("Navigation error: " + e.getMessage()); }
    }
}
