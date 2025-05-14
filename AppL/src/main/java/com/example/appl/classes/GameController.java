package com.example.appl.classes;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameController {
    @FXML
    private Pane circuitPane;
    
    private final List<CircuitNode> nodes = new ArrayList<>();
    private final List<CircuitConnection> connections = new ArrayList<>();
    private CircuitNode firstSelectedNode = null;
    private int firstSelectedIndex = -1;

    private void handleNodeClick(CircuitNode node, MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        if (firstSelectedNode == null) {
            // First node selection
            firstSelectedNode = node;
            firstSelectedIndex = node.findClosestConnectionPoint(x, y);
            if (firstSelectedIndex != -1) {
                node.setSelected(true);
            }
        } else {
            // Second node selection
            int secondSelectedIndex = node.findClosestConnectionPoint(x, y);
            if (secondSelectedIndex != -1 && canCreateConnection(firstSelectedNode, node)) {
                createConnection(firstSelectedNode, node, firstSelectedIndex, secondSelectedIndex);
            }
            
            // Reset selection
            firstSelectedNode.setSelected(false);
            firstSelectedNode = null;
            firstSelectedIndex = -1;
        }
    }

    private boolean canCreateConnection(CircuitNode source, CircuitNode target) {
        // Prevent self-connections
        if (source == target) return false;

        // Prevent connecting outputs to outputs or inputs to inputs
        if (source.getType() == CircuitNode.NodeType.OUTPUT && target.getType() == CircuitNode.NodeType.OUTPUT) return false;
        if (source.getType() == CircuitNode.NodeType.INPUT && target.getType() == CircuitNode.NodeType.INPUT) return false;

        // Prevent connecting to a NOT gate if it already has an input
        if (target.getType() == CircuitNode.NodeType.GATE && target.getGateItem() != null && 
            target.getGateItem().getGateType().equals("NOT") && !target.getInputs().isEmpty()) {
            return false;
        }

        return true;
    }

    private void createConnection(CircuitNode source, CircuitNode target, int sourceIndex, int targetIndex) {
        try {
            CircuitConnection connection = new CircuitConnection(source, target, sourceIndex, targetIndex);
            circuitPane.getChildren().add(connection);
            connections.add(connection);
            connection.updatePath();
            updateCircuit();
        } catch (Exception e) {
            System.err.println("Error creating connection: " + e.getMessage());
        }
    }

    private void updateCircuit() {
        // Reset all gate states
        for (CircuitNode node : nodes) {
            if (node.getType() == CircuitNode.NodeType.GATE) {
                node.setState(false);
            }
        }

        // Evaluate each gate
        boolean changed;
        do {
            changed = false;
            for (CircuitNode node : nodes) {
                if (node.getType() == CircuitNode.NodeType.GATE && node.getGateItem() != null) {
                    boolean newState = evaluateGate(node);
                    if (newState != node.isActive()) {
                        node.setState(newState);
                        changed = true;
                    }
                }
            }
        } while (changed);
    }

    private boolean evaluateGate(CircuitNode gate) {
        if (gate.getGateItem() == null) return false;

        List<CircuitConnection> inputs = gate.getInputs();
        String gateType = gate.getGateItem().getGateType();

        switch (gateType) {
            case "NOT":
                if (inputs.size() == 1) {
                    return !inputs.get(0).getSource().isActive();
                }
                break;
            case "AND":
                if (inputs.size() == 2) {
                    return inputs.get(0).getSource().isActive() && inputs.get(1).getSource().isActive();
                }
                break;
            case "OR":
                if (inputs.size() == 2) {
                    return inputs.get(0).getSource().isActive() || inputs.get(1).getSource().isActive();
                }
                break;
            case "XOR":
                if (inputs.size() == 2) {
                    return inputs.get(0).getSource().isActive() != inputs.get(1).getSource().isActive();
                }
                break;
        }
        return false;
    }

    // ... existing code ...
} 