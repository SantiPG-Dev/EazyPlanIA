package com.eazyplan.presentation.controllers;

import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.services.UserService;
import com.eazyplan.presentation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;

    private final UserService userService = new UserService();

    @FXML
    public void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        try {
            User user = userService.findByUsername(username);
            if (user == null || !user.getPassword().equals(password)) {
                showError("Invalid credentials");
                return;
            }
            SceneManager.setUserData(user);
            SceneManager.switchScene("/presentation/views/DashboardView.fxml");
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    @FXML
    public void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        try {
            User user = userService.register(username, username, username + "@eazyplan.com", password);
            SceneManager.setUserData(user);
            SceneManager.switchScene("/presentation/views/DashboardView.fxml");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }
}
