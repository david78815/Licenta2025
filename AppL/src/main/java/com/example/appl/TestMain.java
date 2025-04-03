package com.example.appl;

import com.example.appl.controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class TestMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load your GameView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Parent root = loader.load();

        // Optionally get the controller if you want to do something special
         GameController controller = loader.getController();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Test GameView");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
