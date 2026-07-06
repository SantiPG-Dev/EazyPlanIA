package com.eazyplan.presentation.controllers;

import com.eazyplan.domain.entities.User;
import com.eazyplan.presentation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        Object data = SceneManager.getUserData();
        if (data instanceof User user) {
            welcomeLabel.setText("Welcome, " + user.getName());
        }
    }

    @FXML
    public void goToWorkout() {
        navigateTo("/presentation/views/WorkoutView.fxml");
    }

    @FXML
    public void goToGroceryList() {
        navigateTo("/presentation/views/GroceryListView.fxml");
    }

    @FXML
    public void goToDiet() {
        navigateTo("/presentation/views/DietView.fxml");
    }

    @FXML
    public void logout() {
        SceneManager.setUserData(null);
        navigateTo("/presentation/views/LoginView.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            SceneManager.switchScene(fxmlPath);
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
}
