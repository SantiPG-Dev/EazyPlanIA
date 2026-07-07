package com.eazyplan;

import com.eazyplan.presentation.SceneManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point de la aplicación JavaFX EazyPlan.
 */
public class EazyPlanApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.init(primaryStage);
        primaryStage.setTitle("EazyPlan");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentation/views/LoginView.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() {
        com.eazyplan.infrastructure.database.DatabaseConfig.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
