package com.example.appl.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LevelSelectController {
    @FXML
    private void onLevel1Click(ActionEvent event) {
        loadGameWithLevel(1, event);
    }

    @FXML
    private void onLevel2Click(ActionEvent event) {
        loadGameWithLevel(2, event);
    }

    @FXML
    private void onLevel3Click(ActionEvent event) {
        loadGameWithLevel(3, event);
    }

    @FXML
    private void onLevel4Click(ActionEvent event) {
        loadGameWithLevel(4, event);
    }

    @FXML
    private void onLevel5Click(ActionEvent event) {
        loadGameWithLevel(5, event);
    }

    @FXML
    private void onLevel6Click(ActionEvent event) {
        loadGameWithLevel(6, event);
    }

    @FXML
    private void onLevel7Click(ActionEvent event) {
        loadGameWithLevel(7, event);
    }

    @FXML
    private void onLevel8Click(ActionEvent event) {
        loadGameWithLevel(8, event);
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appl/main-menu.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/appl/styles.css").toExternalForm());
            
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameWithLevel(int level, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appl/game-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/appl/styles.css").toExternalForm());
            
            GameController controller = loader.getController();
            controller.initializeLevel(level);
            
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 