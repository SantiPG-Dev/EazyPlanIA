package com.eazyplan.presentation;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;
    private static Object userData;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void setUserData(Object data) { userData = data; }
    public static Object getUserData() { return userData; }
}
