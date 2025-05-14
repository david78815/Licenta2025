package com.example.appl.classes;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class TruthTableEntry {
    private final BooleanProperty input1;
    private final BooleanProperty input2;
    private final BooleanProperty expectedOutput;
    private final BooleanProperty actualOutput;

    public TruthTableEntry(boolean input1, boolean input2, boolean expectedOutput) {
        this.input1 = new SimpleBooleanProperty(input1);
        this.input2 = new SimpleBooleanProperty(input2);
        this.expectedOutput = new SimpleBooleanProperty(expectedOutput);
        this.actualOutput = new SimpleBooleanProperty(false);
    }

    // Getters and setters
    public boolean getInput1() {
        return input1.get();
    }

    public boolean getInput2() {
        return input2.get();
    }

    public boolean getExpectedOutput() {
        return expectedOutput.get();
    }

    public boolean getActualOutput() {
        return actualOutput.get();
    }

    public void setActualOutput(boolean value) {
        actualOutput.set(value);
    }

    // Property getters for JavaFX bindings
    public BooleanProperty input1Property() {
        return input1;
    }

    public BooleanProperty input2Property() {
        return input2;
    }

    public BooleanProperty expectedOutputProperty() {
        return expectedOutput;
    }

    public BooleanProperty actualOutputProperty() {
        return actualOutput;
    }
} 