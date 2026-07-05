package com.eazyplan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Entry point de la aplicación JavaFX EazyPlan.
 */
public class EazyPlanApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/presentation/views/LoginView.fxml"));
        StackPane root = loader.load();
        
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("EazyPlan");
        primaryStage.setScene(scene);
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
