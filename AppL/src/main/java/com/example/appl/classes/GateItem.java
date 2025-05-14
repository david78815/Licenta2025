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
        
        shape.setFill(Color.WHITE);
        shape.setStroke(Color.BLACK);
        shape.setStrokeWidth(2);
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
        
        // Curve to bottom
        ArcTo arcTo1 = new ArcTo();
        arcTo1.setX(-GATE_WIDTH/2);
        arcTo1.setY(GATE_HEIGHT/2);
        arcTo1.setRadiusX(GATE_WIDTH * 0.7);
        arcTo1.setRadiusY(GATE_HEIGHT);
        arcTo1.setLargeArcFlag(false);
        arcTo1.setSweepFlag(true);
        path.getElements().add(arcTo1);
        
        // Right curve
        ArcTo arcTo2 = new ArcTo();
        arcTo2.setX(-GATE_WIDTH/2);
        arcTo2.setY(-GATE_HEIGHT/2);
        arcTo2.setRadiusX(GATE_WIDTH * 0.3);
        arcTo2.setRadiusY(GATE_HEIGHT/2);
        arcTo2.setLargeArcFlag(false);
        arcTo2.setSweepFlag(false);
        path.getElements().add(arcTo2);
        
        return path;
    }

    private Shape createNotGate() {
        Group group = new Group();
        
        // Triangle
        Path triangle = new Path();
        triangle.getElements().addAll(
            new MoveTo(-GATE_WIDTH/2, -GATE_HEIGHT/2),
            new LineTo(GATE_WIDTH/2 - 8, 0),
            new LineTo(-GATE_WIDTH/2, GATE_HEIGHT/2),
            new LineTo(-GATE_WIDTH/2, -GATE_HEIGHT/2)
        );
        
        // Output circle
        Circle circle = new Circle(GATE_WIDTH/2, 0, 4);
        
        Shape shape = Shape.union(triangle, circle);
        return shape;
    }

    private Shape createNandGate() {
        Shape andGate = createAndGate();
        Circle circle = new Circle(GATE_WIDTH/2, 0, 4);
        return Shape.union(andGate, circle);
    }

    private Shape createNorGate() {
        Shape orGate = createOrGate();
        Circle circle = new Circle(GATE_WIDTH/2, 0, 4);
        return Shape.union(orGate, circle);
    }

    private Shape createXorGate() {
        Group group = new Group();
        
        // OR gate base
        Shape orGate = createOrGate();
        
        // Extra input arc
        Path extraArc = new Path();
        extraArc.getElements().add(new MoveTo(-GATE_WIDTH/2 - 8, -GATE_HEIGHT/2));
        
        ArcTo arcTo = new ArcTo();
        arcTo.setX(-GATE_WIDTH/2 - 8);
        arcTo.setY(GATE_HEIGHT/2);
        arcTo.setRadiusX(GATE_WIDTH * 0.7);
        arcTo.setRadiusY(GATE_HEIGHT);
        arcTo.setLargeArcFlag(false);
        arcTo.setSweepFlag(true);
        extraArc.getElements().add(arcTo);
        
        Shape shape = Shape.union(orGate, extraArc);
        return shape;
    }

    public String getGateType() {
        return gateType;
    }

    public boolean evaluate(boolean input1, boolean input2) {
        return logicFunction.apply(input1, input2);
    }
}
