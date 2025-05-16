package com.example.appl.classes;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.ArcTo;
import javafx.scene.paint.Color;
import javafx.scene.Group;
import java.util.function.BiFunction;

public class GateItem {
    private final String gateType;
    private final BiFunction<Boolean, Boolean, Boolean> logicFunction;
    private static final double GATE_WIDTH = 40;
    private static final double GATE_HEIGHT = 30;

    public GateItem(String gateType) {
        this.gateType = gateType;
        this.logicFunction = createLogicFunction(gateType);
    }

    private BiFunction<Boolean, Boolean, Boolean> createLogicFunction(String type) {
        return switch (type) {
            case "AND" -> (a, b) -> a && b;
            case "OR" -> (a, b) -> a || b;
            case "NAND" -> (a, b) -> !(a && b);
            case "NOR" -> (a, b) -> !(a || b);
            case "XOR" -> (a, b) -> a ^ b;
            case "NOT" -> (a, b) -> !a;  // NOT gate only uses first input
            default -> throw new IllegalArgumentException("Unknown gate type: " + type);
        };
    }

    public Shape createGateShape() {
        Shape shape = switch (gateType) {
            case "AND" -> createAndGate();
            case "OR" -> createOrGate();
            case "NOT" -> createNotGate();
            case "NAND" -> createNandGate();
            case "NOR" -> createNorGate();
            case "XOR" -> createXorGate();
            default -> new Rectangle(GATE_WIDTH, GATE_HEIGHT);
        };
        
        // Only add the style class to gates that don't have explicit styling
        if (gateType.equals("AND") || gateType.equals("NAND")) {
            shape.getStyleClass().add("gate-shape");
        }
        return shape;
    }

    private Shape createAndGate() {
        Group group = new Group();
        
        // Main rectangle
        Rectangle rect = new Rectangle(-GATE_WIDTH/2, -GATE_HEIGHT/2, GATE_WIDTH * 0.7, GATE_HEIGHT);
        
        // Right arc
        Arc arc = new Arc(
            (-GATE_WIDTH/2 + GATE_WIDTH * 0.7), 0,  // center X,Y
            GATE_WIDTH * 0.3, GATE_HEIGHT/2,        // radiusX,Y
            -90, 180                                // start angle, length
        );
        
        Shape shape = Shape.union(rect, arc);
        return shape;
    }

    private Shape createOrGate() {
        Path path = new Path();
        
        // Start at top left
        path.getElements().add(new MoveTo(-GATE_WIDTH/2, -GATE_HEIGHT/2));
        
        // Create the curved front using a quadratic curve
        ArcTo frontArc = new ArcTo();
        frontArc.setX(-GATE_WIDTH/2);
        frontArc.setY(GATE_HEIGHT/2);
        frontArc.setRadiusX(GATE_WIDTH * 0.8);
        frontArc.setRadiusY(GATE_HEIGHT/2);
        frontArc.setLargeArcFlag(false);
        frontArc.setSweepFlag(true);
        path.getElements().add(frontArc);
        
        // Close the path back to start
        path.getElements().add(new LineTo(-GATE_WIDTH/2, -GATE_HEIGHT/2));
        
        path.setFill(Color.WHITE);
        path.setStroke(Color.BLACK);
        path.setStrokeWidth(2);
        
        return path;
    }

    private Shape createNotGate() {
        // Create the triangle shape
        Path triangle = new Path();
        triangle.getElements().addAll(
            new MoveTo(-GATE_WIDTH/2, -GATE_HEIGHT/2),
            new LineTo(GATE_WIDTH/2 - 8, 0),
            new LineTo(-GATE_WIDTH/2, GATE_HEIGHT/2),
            new LineTo(-GATE_WIDTH/2, -GATE_HEIGHT/2)
        );
        triangle.setFill(Color.WHITE);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeWidth(2);
        
        // Create the output circle
        Circle circle = new Circle(GATE_WIDTH/2, 0, 5);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        // Combine shapes
        Shape combined = Shape.union(triangle, circle);
        combined.setFill(Color.WHITE);
        combined.setStroke(Color.BLACK);
        combined.setStrokeWidth(2);
        
        return combined;
    }

    private Shape createNandGate() {
        Shape andGate = createAndGate();
        
        // Create a larger, more prominent output circle
        Circle circle = new Circle(GATE_WIDTH/2, 0, 6);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        return Shape.union(andGate, circle);
    }

    private Shape createNorGate() {
        Shape orGate = createOrGate();
        
        // Create a larger, more prominent output circle
        Circle circle = new Circle(GATE_WIDTH/2, 0, 6);
        circle.setFill(Color.WHITE);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        
        return Shape.union(orGate, circle);
    }

    private Shape createXorGate() {
        // Create the base OR gate
        Shape orGate = createOrGate();
        
        // Create the extra input curve
        Path extraCurve = new Path();
        extraCurve.getElements().add(new MoveTo(-GATE_WIDTH/2 - 5, -GATE_HEIGHT/2));
        
        ArcTo arcTo = new ArcTo();
        arcTo.setX(-GATE_WIDTH/2 - 5);
        arcTo.setY(GATE_HEIGHT/2);
        arcTo.setRadiusX(GATE_WIDTH * 0.5);
        arcTo.setRadiusY(GATE_HEIGHT);
        arcTo.setLargeArcFlag(false);
        arcTo.setSweepFlag(true);
        extraCurve.getElements().add(arcTo);
        
        // Combine shapes
        Shape combined = Shape.union(orGate, extraCurve);
        combined.getStyleClass().add("gate-shape");
        
        return combined;
    }

    public String getGateType() {
        return gateType;
    }

    public boolean evaluate(boolean input1, boolean input2) {
        return logicFunction.apply(input1, input2);
    }
}
