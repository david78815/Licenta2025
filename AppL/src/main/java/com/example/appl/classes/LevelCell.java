package com.example.appl.classes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LevelCell extends ListCell<LevelModel> {
    private HBox root = new HBox(10); // Horizontal layout, 10px spacing
    private ImageView lockIcon = new ImageView();
    private VBox textArea = new VBox(3);  // Vertical stack: name, description, score/time
    private Label levelNameLabel = new Label();
    private Label descriptionLabel = new Label();
    private Label scoreAndTimeLabel = new Label();

    public LevelCell(){
        root.setAlignment(Pos.CENTER_LEFT);

        lockIcon.setFitHeight(24);
        lockIcon.setFitWidth(24);

        textArea.getChildren().addAll(levelNameLabel,descriptionLabel,scoreAndTimeLabel);

        root.getChildren().addAll(lockIcon,textArea);
    }

    @Override
    protected void updateItem(LevelModel level, boolean empty){
        super.updateItem(level, empty);

        if(empty||level==null){
            setText(null);
            setGraphic(null);
        }else{
            levelNameLabel.setText(level.getLevelName());
            descriptionLabel.setText(level.getDescription());
            scoreAndTimeLabel.setText(String.format("Score: %d   Time:  %s",
                                                        level.getScore(),
                                                        level.getCompletionTime()));

            if(level.isLocked()){
                lockIcon.setImage(new Image(getClass().getResourceAsStream("/icons/lock.png")));
            }else{
                lockIcon.setImage(null);
            }
            setGraphic(root);
            setText(null);
        }



    }
}
