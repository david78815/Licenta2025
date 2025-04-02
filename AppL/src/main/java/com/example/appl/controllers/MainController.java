package com.example.appl.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private Button lvlSelBtn;

    @FXML
    private void onLvlSelBtnClick(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lvlSelect-view.fxml"));
            Parent lvlSelectRoot = loader.load();

            Scene lvlSelectScene = new Scene(lvlSelectRoot);

            Stage stage = (Stage) lvlSelBtn.getScene().getWindow();

            stage.setScene(lvlSelectScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}