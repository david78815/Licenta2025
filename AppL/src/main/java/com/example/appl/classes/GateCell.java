package com.example.appl.classes;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;

/**
 * Custom ListCell implementation for displaying logic gates in a ListView.
 * Supports drag and drop functionality for gate placement.
 */
public class GateCell extends ListCell<GateItem> {
    private final VBox content;
    private final StackPane shapeContainer;
    private final Label label;

    public GateCell() {
        content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        content.setPrefWidth(80);
        content.setPrefHeight(70);
        
        shapeContainer = new StackPane();
        shapeContainer.setPrefSize(50, 50);
        shapeContainer.setMinSize(50, 50);
        shapeContainer.setMaxSize(50, 50);
        shapeContainer.setStyle("-fx-background-color: transparent;");
        shapeContainer.setAlignment(Pos.CENTER);
        
        label = new Label();
        label.getStyleClass().add("gate-label");
        
        content.getChildren().addAll(shapeContainer, label);
        content.getStyleClass().add("gate-list-cell");
    }

    @Override
    protected void updateItem(GateItem item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty || item == null) {
            clearContent();
        } else {
            setupCell(item);
        }
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
        shapeContainer.getChildren().clear();
    }

    private void setupCell(GateItem item) {
        try {
            shapeContainer.getChildren().clear();
            var shape = item.createGateShape();
            shape.setScaleX(1.0);
            shape.setScaleY(1.0);
            shapeContainer.getChildren().add(shape);
            label.setText(item.getGateType());
            setGraphic(content);
            setupDragAndDrop(item);
        } catch (Exception e) {
            handleError(item, e);
        }
    }

    private void setupDragAndDrop(GateItem item) {
        setOnDragDetected(event -> {
            if (item == null) return;

            Dragboard db = startDragAndDrop(TransferMode.COPY);
            
            StackPane dragShape = new StackPane();
            dragShape.setAlignment(Pos.CENTER);
            var shape = item.createGateShape();
            shape.setScaleX(1.0);
            shape.setScaleY(1.0);
            dragShape.getChildren().add(shape);
            dragShape.setStyle("-fx-background-color: transparent;");
            dragShape.setPrefSize(50, 50);
            dragShape.setMinSize(50, 50);
            
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            
            db.setDragView(dragShape.snapshot(sp, null));
            
            ClipboardContent content = new ClipboardContent();
            content.putString(item.getGateType());
            db.setContent(content);
            
            event.consume();
        });
    }

    private void handleError(GateItem item, Exception e) {
        System.err.println("Failed to create shape for " + item.getGateType() + ": " + e.getMessage());
        label.setText(item.getGateType() + " (Error)");
        shapeContainer.getChildren().clear();
    }
}
