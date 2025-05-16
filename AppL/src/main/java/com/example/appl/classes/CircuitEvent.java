package com.example.appl.classes;

import javafx.event.Event;
import javafx.event.EventType;

public class CircuitEvent extends Event {
    public static final EventType<CircuitEvent> CIRCUIT_CHANGED = 
        new EventType<>(Event.ANY, "CIRCUIT_CHANGED");
    
    private final CircuitNode source;
    
    public CircuitEvent(EventType<CircuitEvent> eventType, CircuitNode source) {
        super(eventType);
        this.source = source;
    }
    
    public CircuitNode getCircuitNode() {
        return source;
    }
} 