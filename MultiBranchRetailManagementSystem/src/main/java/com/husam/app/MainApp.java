package com.husam.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        SceneManager.switchScene("/view/LoginView.fxml", "Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
