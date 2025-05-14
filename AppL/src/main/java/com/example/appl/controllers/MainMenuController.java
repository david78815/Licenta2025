package com.example.appl.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

public class MainMenuController {
    @FXML private Button startGameButton;
    @FXML private Button tutorialButton;
    @FXML private Button exitButton;

    @FXML
    private void onStartGameClick() {
        try {
            // Load the level select view
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/appl/level-select.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/appl/styles.css").toExternalForm());
            
            // Get the current stage
            Stage stage = (Stage) startGameButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Logic Gate Simulator - Level Select");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onTutorialClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appl/tutorial.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/appl/styles.css").toExternalForm());
            
            Stage stage = (Stage) tutorialButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Logic Gate Simulator - Tutorial");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExitClick() {
        Platform.exit();
    }
} 