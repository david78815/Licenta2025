package com.example.appl.classes;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
public class GateCell extends ListCell<GateItem> {

    private VBox container = new VBox(2);
    private ImageView iconView = new ImageView();
    private Label nameLabel = new Label();

    public GateCell(){

        iconView.setFitWidth(32);
        iconView.setFitHeight(32);
        iconView.setPreserveRatio(true);

        container.getChildren().addAll(iconView, nameLabel);
        container.setStyle("-fx-alignment: center;");
    }
    @Override
    protected void updateItem(GateItem item, boolean empty){
        super.updateItem(item, empty);
        if(empty||item==null){
            setText(null);
            setGraphic(null);
        }else{
            iconView.setImage(new Image(getClass().getResourceAsStream(item.getIconPath())));

            nameLabel.setText(item.getGateType());
            setGraphic(container);
            setText(null);
        }
    }

}
