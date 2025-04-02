package com.example.appl.controllers;
import com.example.appl.classes.LevelCell;
import com.example.appl.classes.LevelModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class LvlSelController {
        @FXML
        private ListView<LevelModel> levelListView;

        private ObservableList<LevelModel> levelData;

        @FXML
        public void initialize(){
            levelData = FXCollections.observableArrayList(
                    new LevelModel("Level 1", "PlaceHolder", 0,"--:--",false ),
                    new LevelModel("Level 2","PlaceHolder"),
                    new LevelModel("Level 3","PlaceHolder"),
                    new LevelModel("Level 4","PlaceHolder"),
                    new LevelModel("Level 5","PlaceHolder")
            );
            levelListView.setItems(levelData);
            levelListView.setCellFactory(listView -> new LevelCell());
        }

        @FXML
        private Button lvlSelBack;

        @FXML
        private void onBackLvlSelButtonClicked(){
            try{
                FXMLLoader loader  = new FXMLLoader(getClass().getResource("main-view.fxml"));
                Parent mainViewRoot = loader.load();

                Stage stage = (Stage) lvlSelBack.getScene().getWindow();

                Scene mainViewScene = new Scene(mainViewRoot);

                stage.setScene(mainViewScene);
                stage.show();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}
