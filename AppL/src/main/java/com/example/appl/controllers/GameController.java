package com.example.appl.controllers;

import com.example.appl.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller class for the game view.
 * Handles the circuit building interface and game logic.
 */
public class GameController {
    @FXML private ListView<GateItem> gateListView;
    @FXML private AnchorPane circuitArea;
    @FXML private ToggleButton source1Toggle;
    @FXML private ToggleButton source2Toggle;
    @FXML private TableView<TruthTableEntry> truthTable;
    @FXML private TableColumn<TruthTableEntry, Boolean> input1Col;
    @FXML private TableColumn<TruthTableEntry, Boolean> input2Col;
    @FXML private TableColumn<TruthTableEntry, Boolean> expectedOutputCol;
    @FXML private TableColumn<TruthTableEntry, Boolean> actualOutputCol;
    @FXML private Button validateButton;
    @FXML private Button clearButton;
    @FXML private Label levelLabel;

    private CircuitNode source1;
    private CircuitNode source2;
    private CircuitNode destination;
    private final ObservableList<GateItem> gateItems = FXCollections.observableArrayList();
    private final ObservableList<TruthTableEntry> truthTableData = FXCollections.observableArrayList();
    private final List<CircuitNode> gates = new ArrayList<>();
    private CircuitNode selectedNode;
    private GateItem currentGateType; // Store the current gate type for the truth table
    private int currentLevel;
    private String levelDescription;

    /**
     * Initializes the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        setupGateList();
        setupTruthTable();
        setupCircuitArea();
        setupControls();
    }

    /**
     * Sets up the list of available logic gates.
     */
    private void setupGateList() {
        gateItems.addAll(
            new GateItem("AND"),
            new GateItem("OR"),
            new GateItem("NOT"),
            new GateItem("NAND"),
            new GateItem("NOR"),
            new GateItem("XOR")
        );
        
        Objects.requireNonNull(gateListView, "gateListView not injected by FXML loader");
        gateListView.setItems(gateItems);
        gateListView.setCellFactory(listView -> new GateCell());
    }

    /**
     * Sets up the truth table with initial values.
     */
    private void setupTruthTable() {
        Objects.requireNonNull(truthTable, "truthTable not injected by FXML loader");
        Objects.requireNonNull(input1Col, "input1Col not injected by FXML loader");
        Objects.requireNonNull(input2Col, "input2Col not injected by FXML loader");
        Objects.requireNonNull(expectedOutputCol, "expectedOutputCol not injected by FXML loader");
        Objects.requireNonNull(actualOutputCol, "actualOutputCol not injected by FXML loader");

        // Initialize truth table columns
        input1Col.setCellValueFactory(new PropertyValueFactory<>("input1"));
        input2Col.setCellValueFactory(new PropertyValueFactory<>("input2"));
        expectedOutputCol.setCellValueFactory(new PropertyValueFactory<>("expectedOutput"));
        actualOutputCol.setCellValueFactory(new PropertyValueFactory<>("actualOutput"));

        // Create truth table data for AND gate (default)
        truthTableData.addAll(
            new TruthTableEntry(false, false, false),
            new TruthTableEntry(false, true, false),
            new TruthTableEntry(true, false, false),
            new TruthTableEntry(true, true, true)
        );
        truthTable.setItems(truthTableData);
    }

    private void setupCircuitArea() {
        Objects.requireNonNull(circuitArea, "circuitArea not injected by FXML loader");
        
        // Create source and destination nodes
        source1 = new CircuitNode(CircuitNode.NodeType.INPUT);
        source2 = new CircuitNode(CircuitNode.NodeType.INPUT);
        destination = new CircuitNode(CircuitNode.NodeType.OUTPUT);

        // Position nodes
        source1.setLayoutX(50);
        source1.setLayoutY(100);
        source2.setLayoutX(50);
        source2.setLayoutY(200);
        destination.setLayoutX(500);
        destination.setLayoutY(150);

        // Add nodes to circuit area
        circuitArea.getChildren().addAll(source1, source2, destination);

        // Setup drag and drop for gates
        circuitArea.setOnDragOver(this::handleDragOver);
        circuitArea.setOnDragDropped(this::handleDragDropped);

        // Setup node click handlers
        setupNodeClickHandlers(source1);
        setupNodeClickHandlers(source2);
        setupNodeClickHandlers(destination);
    }

    private void setupNodeClickHandlers(CircuitNode node) {
        node.setOnMouseClicked(event -> handleNodeClick(node, event));
    }

    private void handleNodeClick(CircuitNode node, MouseEvent event) {
        if (selectedNode == null) {
            selectedNode = node;
            node.setSelected(true);
        } else if (selectedNode != node) {
            createConnection(selectedNode, node);
            selectedNode.setSelected(false);
            selectedNode = null;
        } else {
            selectedNode.setSelected(false);
            selectedNode = null;
        }
        event.consume();
    }

    private void createConnection(CircuitNode source, CircuitNode target) {
        if (isValidConnection(source, target)) {
            // Determine connection points based on node types
            int sourceIndex = getSourceConnectionIndex(source);
            int targetIndex = getTargetConnectionIndex(target);
            
            if (sourceIndex >= 0 && targetIndex >= 0) {
                CircuitConnection connection = new CircuitConnection(source, target, sourceIndex, targetIndex);
                circuitArea.getChildren().add(0, connection); // Add at index 0 to render below nodes
                updateCircuit();
            }
        }
    }

    private int getSourceConnectionIndex(CircuitNode node) {
        if (node.getType() == CircuitNode.NodeType.INPUT) {
            return 0; // Source has only one output point
        } else if (node.getType() == CircuitNode.NodeType.GATE) {
            return 2; // Gate's output point is at index 2
        }
        return -1;
    }

    private int getTargetConnectionIndex(CircuitNode node) {
        if (node.getType() == CircuitNode.NodeType.OUTPUT) {
            return 0; // Destination has only one input point
        } else if (node.getType() == CircuitNode.NodeType.GATE) {
            // For gates, use the first available input point
            return node.getInputs().isEmpty() ? 0 : 1;
        }
        return -1;
    }

    private boolean isValidConnection(CircuitNode source, CircuitNode target) {
        if (source == target) return false;
        if (source.getType() == CircuitNode.NodeType.OUTPUT) return false;
        if (target.getType() == CircuitNode.NodeType.INPUT) return false;
        
        if (target.getType() == CircuitNode.NodeType.GATE) {
            GateItem gate = target.getGateItem();
            if (gate != null && gate.getGateType().equals("NOT")) {
                return target.getInputs().isEmpty(); // NOT gate can only have one input
            }
            return target.getInputs().size() < 2; // Other gates can have up to two inputs
        }
        
        // Destination node can only have one input
        if (target.getType() == CircuitNode.NodeType.OUTPUT) {
            return target.getInputs().isEmpty();
        }
        
        return true;
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        boolean success = false;
        if (event.getDragboard().hasString()) {
            String gateType = event.getDragboard().getString();
            Optional<GateItem> gateItem = gateItems.stream()
                .filter(item -> item.getGateType().equals(gateType))
                .findFirst();

            if (gateItem.isPresent()) {
                CircuitNode gate = new CircuitNode(CircuitNode.NodeType.GATE);
                gate.setGateItem(gateItem.get());
                gate.setLayoutX(event.getX());
                gate.setLayoutY(event.getY());
                
                setupNodeClickHandlers(gate);
                gates.add(gate);
                circuitArea.getChildren().add(gate);
                success = true;

                // Update truth table for the new gate type
                updateTruthTableForGate(gateItem.get());
            }
        }
        event.setDropCompleted(success);
        event.consume();
    }

    private void updateTruthTableForGate(GateItem gate) {
        currentGateType = gate;
        truthTableData.clear();
        // Generate truth table entries based on gate type
        boolean[][] inputs = {{false, false}, {false, true}, {true, false}, {true, true}};
        for (boolean[] input : inputs) {
            boolean output = gate.evaluate(input[0], input[1]);
            truthTableData.add(new TruthTableEntry(input[0], input[1], output));
        }
    }

    private void updateCircuit() {
        // Reset all gate states first
        for (CircuitNode gate : gates) {
            gate.setState(false);
        }
        destination.setState(false);

        // Update source states
        source1.setState(source1Toggle.isSelected());
        source2.setState(source2Toggle.isSelected());

        // Evaluate gates in order
        boolean changed;
        do {
            changed = false;
            for (CircuitNode gate : gates) {
                if (gate.getGateItem() != null) {
                    List<CircuitConnection> inputs = gate.getInputs();
                    boolean input1 = inputs.size() > 0 ? inputs.get(0).getSource().isActive() : false;
                    boolean input2 = inputs.size() > 1 ? inputs.get(1).getSource().isActive() : false;
                    
                    boolean newState = gate.getGateItem().evaluate(input1, input2);
                    if (newState != gate.isActive()) {
                        gate.setState(newState);
                        changed = true;
                    }
                }
            }
        } while (changed);

        // Update destination node
        if (!destination.getInputs().isEmpty()) {
            destination.setState(destination.getInputs().get(0).getSource().isActive());
        }

        // Update truth table actual outputs
        boolean input1State = source1.isActive();
        boolean input2State = source2.isActive();
        for (TruthTableEntry entry : truthTableData) {
            if (entry.getInput1() == input1State && entry.getInput2() == input2State) {
                entry.setActualOutput(destination.isActive());
                break;
            }
        }
        truthTable.refresh();
    }

    private void validateCircuit() {
        boolean isValid = true;
        int correctOutputs = 0;
        int totalOutputs = truthTableData.size();

        // Test all possible input combinations
        for (TruthTableEntry entry : truthTableData) {
            // Set inputs according to truth table entry
            source1Toggle.setSelected(entry.getInput1());
            source2Toggle.setSelected(entry.getInput2());
            updateCircuit();

            // Compare actual output with expected output
            if (destination.isActive() == entry.getExpectedOutput()) {
                correctOutputs++;
            } else {
                isValid = false;
            }
        }

        // Create appropriate message
        String message;
        if (isValid) {
            message = "Circuit is correct! All outputs match the expected values.";
        } else {
            message = String.format("Circuit is incorrect. %d out of %d outputs are correct.", 
                                  correctOutputs, totalOutputs);
        }

        Alert alert = new Alert(isValid ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle("Circuit Validation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // Reset inputs to their previous state
        updateCircuit();
    }

    private void clearCircuit() {
        // Remove all gates and connections
        List<Node> toRemove = new ArrayList<>();
        for (Node node : circuitArea.getChildren()) {
            if (node instanceof CircuitConnection || 
                (node instanceof CircuitNode && ((CircuitNode) node).getType() == CircuitNode.NodeType.GATE)) {
                toRemove.add(node);
            }
        }
        circuitArea.getChildren().removeAll(toRemove);
        gates.clear();
        selectedNode = null;
        
        // Reset source states
        source1.setState(false);
        source2.setState(false);
        source1Toggle.setSelected(false);
        source2Toggle.setSelected(false);
        
        // Reset truth table actual outputs
        for (TruthTableEntry entry : truthTableData) {
            entry.setActualOutput(false);
        }
    }

    private void setupControls() {
        // Setup source toggles
        source1Toggle.setOnAction(e -> {
            source1.setState(source1Toggle.isSelected());
            updateCircuit();
        });

        source2Toggle.setOnAction(e -> {
            source2.setState(source2Toggle.isSelected());
            updateCircuit();
        });

        // Setup validate button
        validateButton.setOnAction(e -> validateCircuit());

        // Setup clear button
        clearButton.setOnAction(e -> clearCircuit());
    }

    public void initializeLevel(int level) {
        this.currentLevel = level;
        switch (level) {
            case 1 -> {
                levelDescription = "Introduction to Basic Gates";
                gateItems.setAll(new GateItem("AND"), new GateItem("OR"), new GateItem("NOT"));
            }
            case 2 -> {
                levelDescription = "Create an AND Gate Circuit";
                gateItems.setAll(new GateItem("AND"));
                setupAndGateLevel();
            }
            case 3 -> {
                levelDescription = "Create an OR Gate Circuit";
                gateItems.setAll(new GateItem("OR"));
                setupOrGateLevel();
            }
            case 4 -> {
                levelDescription = "Create a NOT Gate Circuit";
                gateItems.setAll(new GateItem("NOT"));
                setupNotGateLevel();
            }
            case 5 -> {
                levelDescription = "Create a NAND Gate Circuit";
                gateItems.setAll(new GateItem("AND"), new GateItem("NOT"));
                setupNandGateLevel();
            }
            case 6 -> {
                levelDescription = "Create a NOR Gate Circuit";
                gateItems.setAll(new GateItem("OR"), new GateItem("NOT"));
                setupNorGateLevel();
            }
            case 7 -> {
                levelDescription = "Create an XOR Gate Circuit";
                gateItems.setAll(new GateItem("AND"), new GateItem("OR"), new GateItem("NOT"));
                setupXorGateLevel();
            }
            case 8 -> {
                levelDescription = "Create a Complex Circuit";
                gateItems.setAll(
                    new GateItem("AND"),
                    new GateItem("OR"),
                    new GateItem("NOT"),
                    new GateItem("NAND"),
                    new GateItem("NOR"),
                    new GateItem("XOR")
                );
                setupComplexLevel();
            }
            default -> {
                levelDescription = "Custom Level";
                setupDefaultLevel();
            }
        }
        updateLevelDisplay();
    }

    private void setupAndGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, false),
            new TruthTableEntry(false, true, false),
            new TruthTableEntry(true, false, false),
            new TruthTableEntry(true, true, true)
        );
    }

    private void setupOrGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, false),
            new TruthTableEntry(false, true, true),
            new TruthTableEntry(true, false, true),
            new TruthTableEntry(true, true, true)
        );
    }

    private void setupNotGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, true),
            new TruthTableEntry(true, false, false)
        );
    }

    private void setupNandGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, true),
            new TruthTableEntry(false, true, true),
            new TruthTableEntry(true, false, true),
            new TruthTableEntry(true, true, false)
        );
    }

    private void setupNorGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, true),
            new TruthTableEntry(false, true, false),
            new TruthTableEntry(true, false, false),
            new TruthTableEntry(true, true, false)
        );
    }

    private void setupXorGateLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, false),
            new TruthTableEntry(false, true, true),
            new TruthTableEntry(true, false, true),
            new TruthTableEntry(true, true, false)
        );
    }

    private void setupComplexLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, true),
            new TruthTableEntry(false, true, false),
            new TruthTableEntry(true, false, false),
            new TruthTableEntry(true, true, true)
        );
    }

    private void setupDefaultLevel() {
        truthTableData.clear();
        truthTableData.addAll(
            new TruthTableEntry(false, false, false),
            new TruthTableEntry(false, true, false),
            new TruthTableEntry(true, false, false),
            new TruthTableEntry(true, true, true)
        );
    }

    private void updateLevelDisplay() {
        levelLabel.setText("Level " + currentLevel + ": " + levelDescription);
    }

    @FXML
    private void onBackToLevelsClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/appl/level-select.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/com/example/appl/styles.css").toExternalForm());
            
            Stage stage = (Stage) levelLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

