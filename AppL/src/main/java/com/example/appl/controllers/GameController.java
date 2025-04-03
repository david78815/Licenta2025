package com.example.appl.controllers;

import com.example.appl.classes.GateCell;
import com.example.appl.classes.GateItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

public class GameController {

    @FXML
    private ListView<GateItem> gateListView;

    @FXML
    private AnchorPane circuitArea;

    private ObservableList<GateItem> gateItems;

    @FXML
    public void initialize(){
        gateItems = FXCollections.observableArrayList(
                new GateItem("AND","/icons/AND.png"),
                new GateItem("OR","/icons/OR.png"),
                new GateItem("NOT","/icons/NOT.png"),
                new GateItem("NAND","/icons/NAND.png"),
                new GateItem("NOR","/icons/NOR.png"),
                new GateItem("XOR","/icons/XOR.png")
        );

        gateListView.setItems(gateItems);
        gateListView.setCellFactory(listView -> new GateCell());
    }

}
