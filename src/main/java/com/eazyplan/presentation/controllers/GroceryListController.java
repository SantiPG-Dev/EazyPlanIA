package com.eazyplan.presentation.controllers;

import com.eazyplan.domain.entities.GroceryList;
import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.services.GroceryListService;
import com.eazyplan.presentation.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class GroceryListController {

    @FXML private ListView<GroceryList> groceryListsView;
    @FXML private Button newGroceryButton;

    private final GroceryListService groceryListService = new GroceryListService();

    @FXML
    public void initialize() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;
        groceryListsView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(GroceryList item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText((item.getCreatedAt() != null ? item.getCreatedAt() : "No date")
                            + (item.isPurchased() ? " ✓" : ""));
                }
            }
        });
        loadLists(user);
    }

    private void loadLists(User user) {
        groceryListsView.getItems().setAll(groceryListService.getUserLists(user.getId()));
    }

    @FXML
    public void handleNewGrocery() {
        Object data = SceneManager.getUserData();
        if (!(data instanceof User user)) return;
        GroceryList list = new GroceryList(user);
        groceryListService.createList(list, user);
        loadLists(user);
    }

    @FXML
    public void goBack() {
        try { SceneManager.switchScene("/presentation/views/DashboardView.fxml"); }
        catch (Exception e) { System.err.println("Navigation error: " + e.getMessage()); }
    }
}
