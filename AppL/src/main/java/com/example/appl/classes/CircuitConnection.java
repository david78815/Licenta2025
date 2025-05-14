package com.example.appl.classes;

import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.StrokeLineCap;
import javafx.application.Platform;

public class CircuitConnection extends Path {
    private final CircuitNode source;
    private final CircuitNode target;
    private final MoveTo moveTo;
    private final LineTo lineTo;
    private final int sourceIndex;
    private final int targetIndex;

    public CircuitConnection(CircuitNode source, CircuitNode target, int sourceIndex, int targetIndex) {
        this.source = source;
        this.target = target;
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
        
        // Create the path elements
        moveTo = new MoveTo();
        lineTo = new LineTo();
        getElements().addAll(moveTo, lineTo);
        
        // Add style class for CSS styling
        getStyleClass().add("circuit-connection");
        
        // Set line properties
        setStrokeWidth(2);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setMouseTransparent(false);
        
        // Add this connection to both nodes
        source.addOutput(this);
        target.addInput(this);

        // Add right-click handler for deletion
        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                deleteConnection();
            }
        });

        // Initial path update using Platform.runLater to ensure parent is set
        Platform.runLater(() -> {
            updatePath();
            toBack();
        });
    }

    public void updatePath() {
        if (getParent() == null) return;

        try {
            // Get the connection points from source and target nodes
            Point2D sourcePoint = source.getConnectionPoint(false, sourceIndex);
            Point2D targetPoint = target.getConnectionPoint(true, targetIndex);

            if (sourcePoint == null || targetPoint == null) {
                setVisible(false);
                return;
            }

            // Convert points to the parent pane's coordinate space
            Point2D sourceInParent = getParent().sceneToLocal(sourcePoint);
            Point2D targetInParent = getParent().sceneToLocal(targetPoint);

            // Update the path
            moveTo.setX(sourceInParent.getX());
            moveTo.setY(sourceInParent.getY());
            lineTo.setX(targetInParent.getX());
            lineTo.setY(targetInParent.getY());

            // Make sure the connection is visible
            setVisible(true);

        } catch (Exception e) {
            System.err.println("Error updating connection path: " + e.getMessage());
            setVisible(false);
        }
    }

    public void deleteConnection() {
        // Remove from parent
        if (getParent() != null) {
            ((Pane) getParent()).getChildren().remove(this);
        }
        
        // Remove from nodes
        source.getOutputs().remove(this);
        target.getInputs().remove(this);
        
        // Update circuit state
        if (target.getType() == CircuitNode.NodeType.GATE) {
            target.setState(false); // Reset gate state when connection is removed
        }
    }

    public CircuitNode getSource() {
        return source;
    }

    public CircuitNode getTarget() {
        return target;
    }

    public int getSourceIndex() {
        return sourceIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }
}