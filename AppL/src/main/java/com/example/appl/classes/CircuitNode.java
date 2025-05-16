package com.example.appl.classes;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.Node;

/**
 * Represents a node in the circuit (source, gate, or destination).
 */
public class CircuitNode extends StackPane {
    public enum NodeType {
        INPUT, OUTPUT, GATE
    }

    private static final double NODE_RADIUS = 15;
    private static final double CONNECTION_POINT_RADIUS = 5;
    private static final int MAX_CONNECTIONS = 8;

    private final Circle mainCircle;
    private final List<Circle> connectionPoints;
    private final List<CircuitConnection> inputs;
    private final List<CircuitConnection> outputs;
    private final NodeType type;
    private boolean active;
    private boolean selected;
    private GateItem gateItem;
    private final Text label;

    public CircuitNode(NodeType type) {
        this.type = type;
        this.active = false;
        this.selected = false;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.connectionPoints = new ArrayList<>();

        // Create main circle (only visible for INPUT/OUTPUT nodes)
        mainCircle = new Circle(NODE_RADIUS);
        mainCircle.getStyleClass().add("circuit-node");

        // Create label
        label = new Text(getNodeLabel());
        label.setFont(Font.font(14));
        label.setFill(Color.BLACK);

        // Only add mainCircle for INPUT/OUTPUT nodes
        if (type != NodeType.GATE) {
            getChildren().addAll(mainCircle, label);
        } else {
            getChildren().add(label);
        }

        // Create connection points
        setupConnectionPoints();
        
        // Setup dragging
        setupDragging();

        // Setup mouse event handlers
        if (type == NodeType.GATE) {
            setOnMouseEntered(e -> setSelected(true));
            setOnMouseExited(e -> setSelected(false));
            setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    deleteGate();
                    event.consume();
                }
            });
        }

        // Set minimum size
        setMinSize(NODE_RADIUS * 2, NODE_RADIUS * 2);
        setPrefSize(NODE_RADIUS * 2, NODE_RADIUS * 2);
    }

    private String getNodeLabel() {
        return switch (type) {
            case INPUT -> "IN";
            case OUTPUT -> "OUT";
            case GATE -> "";
        };
    }

    private void setupConnectionPoints() {
        // Clear existing connection points
        for (Circle point : connectionPoints) {
            getChildren().remove(point);
        }
        connectionPoints.clear();
        
        int numPoints;
        switch (type) {
            case INPUT -> numPoints = 1;  // One output point
            case OUTPUT -> numPoints = 1;  // One input point
            case GATE -> {
                if (gateItem != null && gateItem.getGateType().equals("NOT")) {
                    numPoints = 2;  // One input, one output
                } else {
                    numPoints = 3;  // Two inputs, one output
                }
            }
            default -> numPoints = 0;
        }
        
        for (int i = 0; i < numPoints; i++) {
            Circle point = new Circle(CONNECTION_POINT_RADIUS);
            point.setFill(Color.GRAY);
            point.setStroke(Color.WHITE);
            point.setStrokeWidth(1);
            point.getStyleClass().add("connection-point");
            connectionPoints.add(point);
            getChildren().add(point);
            updateConnectionPointPosition(i);
        }
    }

    private void updateConnectionPointPosition(int index) {
        if (connectionPoints.isEmpty() || index >= connectionPoints.size()) return;
        
        Circle point = connectionPoints.get(index);
        double x = 0, y = 0;
        double offset = NODE_RADIUS * 1.2;  // Reduced offset to match gate size
        
        switch (type) {
            case INPUT -> {
                // Single output point on the right
                x = offset;
                y = 0;
            }
            case OUTPUT -> {
                // Single input point on the left
                x = -offset;
                y = 0;
            }
            case GATE -> {
                if (gateItem != null && gateItem.getGateType().equals("NOT")) {
                    // NOT gate: 1 input on left, 1 output on right
                    if (index == 0) {
                        x = -offset;  // Input
                        y = 0;
                    } else {
                        x = offset;   // Output
                        y = 0;
                    }
                } else {
                    // Other gates: 2 inputs on left, 1 output on right
                    if (index < 2) {
                        x = -offset;  // Inputs
                        y = (index == 0) ? -NODE_RADIUS * 0.7 : NODE_RADIUS * 0.7;
                    } else {
                        x = offset;   // Output
                        y = 0;
                    }
                }
            }
        }
        
        point.setTranslateX(x);
        point.setTranslateY(y);
    }

    public int findClosestConnectionPoint(double x, double y) {
        Point2D clickPoint = new Point2D(x, y);
        double minDistance = Double.MAX_VALUE;
        int closestIndex = -1;

        for (int i = 0; i < connectionPoints.size(); i++) {
            Circle point = connectionPoints.get(i);
            Point2D pointPos = point.localToScene(point.getBoundsInLocal().getCenterX(), point.getBoundsInLocal().getCenterY());
            double distance = clickPoint.distance(pointPos);
            
            if (distance < minDistance && distance < NODE_RADIUS * 3) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public Point2D getConnectionPoint(boolean isInput, int index) {
        if (index >= 0 && index < connectionPoints.size()) {
            Circle point = connectionPoints.get(index);
            return point.localToScene(point.getBoundsInLocal().getCenterX(), point.getBoundsInLocal().getCenterY());
        }
        return null;
    }

    private void setupDragging() {
        final double[] dragDelta = new double[2];

        setOnMousePressed(event -> {
            dragDelta[0] = getLayoutX() - event.getSceneX();
            dragDelta[1] = getLayoutY() - event.getSceneY();
            event.consume();
        });

        setOnMouseDragged(event -> {
            double newX = event.getSceneX() + dragDelta[0];
            double newY = event.getSceneY() + dragDelta[1];
            
            // Ensure the node stays within its parent bounds
            if (getParent() != null) {
                Bounds parentBounds = getParent().getLayoutBounds();
                newX = Math.max(0, Math.min(newX, parentBounds.getWidth() - getWidth()));
                newY = Math.max(0, Math.min(newY, parentBounds.getHeight() - getHeight()));
            }
            
            setLayoutX(newX);
            setLayoutY(newY);
            
            // Update all connected wires
            updateConnections();
            event.consume();
        });
    }

    private void updateConnections() {
        for (CircuitConnection connection : inputs) {
            connection.updatePath();
        }
        for (CircuitConnection connection : outputs) {
            connection.updatePath();
        }
    }

    public void setState(boolean active) {
        this.active = active;
        updateVisualState();
        
        // Update connection points
        updateConnectionPointStates();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateVisualState();
    }

    private void updateVisualState() {
        if (type != NodeType.GATE) {
            mainCircle.getStyleClass().removeAll("active", "selected");
            if (active) mainCircle.getStyleClass().add("active");
            if (selected) mainCircle.getStyleClass().add("selected");
        } else if (gateItem != null) {
            // Update the gate shape if it exists
            Node gateShape = ((Group)getChildren().get(1)).getChildren().get(0);
            gateShape.getStyleClass().removeAll("active", "selected");
            if (active) gateShape.getStyleClass().add("active");
            if (selected) gateShape.getStyleClass().add("selected");
        }
    }

    public void addInput(CircuitConnection connection) {
        if (!inputs.contains(connection)) {
            inputs.add(connection);
        }
    }

    public void addOutput(CircuitConnection connection) {
        if (!outputs.contains(connection)) {
            outputs.add(connection);
        }
    }

    public NodeType getType() {
        return type;
    }

    public List<CircuitConnection> getInputs() {
        return inputs;
    }

    public List<CircuitConnection> getOutputs() {
        return outputs;
    }

    public boolean isActive() {
        return active;
    }

    public void setGateItem(GateItem gateItem) {
        this.gateItem = gateItem;
        if (gateItem != null && type == NodeType.GATE) {
            getChildren().clear();
            var shape = gateItem.createGateShape();
            shape.setScaleX(1.0);
            shape.setScaleY(1.0);
            
            // Create a container for the gate shape
            Group gateGroup = new Group(shape);
            
            // Only add the gate shape and label
            getChildren().addAll(label, gateGroup);
            
            // Add connection points on top
            setupConnectionPoints();
            
            // Make sure the connection points are visible
            connectionPoints.forEach(point -> point.toFront());
            
            // Update visual state to apply any existing state
            updateVisualState();
        }
    }

    public GateItem getGateItem() {
        return gateItem;
    }

    private void updateConnectionPointStates() {
        if (type == NodeType.GATE) {
            // Update input connection points
            for (int i = 0; i < inputs.size() && i < 2; i++) {
                Circle point = connectionPoints.get(i);
                boolean inputActive = inputs.get(i).getSource().isActive();
                point.setFill(inputActive ? Color.valueOf("#4CAF50") : Color.GRAY);
            }
            
            // Update output connection point
            int outputIndex = gateItem != null && gateItem.getGateType().equals("NOT") ? 1 : 2;
            if (outputIndex < connectionPoints.size()) {
                connectionPoints.get(outputIndex).setFill(active ? Color.valueOf("#4CAF50") : Color.GRAY);
            }
        } else if (type == NodeType.INPUT) {
            // Update output connection point
            if (!connectionPoints.isEmpty()) {
                connectionPoints.get(0).setFill(active ? Color.valueOf("#4CAF50") : Color.GRAY);
            }
        } else if (type == NodeType.OUTPUT) {
            // Update input connection point
            if (!connectionPoints.isEmpty()) {
                boolean inputActive = !inputs.isEmpty() && inputs.get(0).getSource().isActive();
                connectionPoints.get(0).setFill(inputActive ? Color.valueOf("#4CAF50") : Color.GRAY);
            }
        }
    }

    public void deleteGate() {
        if (type != NodeType.GATE) return;
        
        // First, delete all connections
        List<CircuitConnection> connectionsToRemove = new ArrayList<>();
        connectionsToRemove.addAll(inputs);
        connectionsToRemove.addAll(outputs);
        
        for (CircuitConnection connection : connectionsToRemove) {
            connection.deleteConnection();
        }
        
        // Clear the lists
        inputs.clear();
        outputs.clear();
        
        // Then remove the gate from its parent
        if (getParent() != null) {
            Pane parent = (Pane) getParent();
            parent.getChildren().remove(this);
            
            // Find and call updateCircuit on the GameController
            parent.fireEvent(new CircuitEvent(CircuitEvent.CIRCUIT_CHANGED, this));
        }
    }
} 