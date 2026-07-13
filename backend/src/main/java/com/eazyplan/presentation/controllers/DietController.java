package com.eazyplan.presentation.controllers;

import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.services.DietService;
import com.eazyplan.domain.services.UserService;
import com.eazyplan.presentation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.time.LocalDate;

public class DietController {

    @FXML private ListView<Diet> dietsListView;
    @FXML private Button newDietButton;
    @FXML private Button deleteDietButton;

    private final DietService dietService = new DietService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;

        dietsListView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Diet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String name = item.getName() != null ? item.getName() : "Unnamed";
                    String type = item.getDietType() != null ? item.getDietType().name() : "";
                    setText(name + " [" + type + "] — " + item.getDailyCalories() + " kcal");
                }
            }
        });

        loadDiets(user);
    }

    private void loadDiets(User user) {
        dietsListView.getItems().setAll(dietService.getUserDiets(user.getId()));
    }

    @FXML
    public void handleCreateDiet() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;
        Diet diet = new Diet();
        diet.setName("New Diet");
        diet.setDietType(Diet.DietType.BALANCED);
        diet.setStartDate(LocalDate.now());
        dietService.createDiet(diet, userService, user.getId());
        loadDiets(user);
    }

    @FXML
    public void handleDeleteDiet() {
        Diet selected = dietsListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        dietService.deleteDiet(selected);
        Object data = SceneManager.getUserData();
        if (data instanceof User user) loadDiets(user);
    }

    @FXML
    public void goBack() {
        try { SceneManager.switchScene("/presentation/views/DashboardView.fxml"); }
        catch (Exception e) { System.err.println("Navigation error: " + e.getMessage()); }
    }
}
