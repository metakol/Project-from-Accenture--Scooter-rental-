package com.example.program;

import com.example.dependencies.Scooter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CustomPane {

    private Scooter scooter;
    private AnchorPane paneSelected;
    private AnchorPane paneParking;
    private Label labelSelectionInfo;
    private Label labelSelectedScooterBatteryCharge;
    private Label labelSelectedScooterModel;
    private Label labelSelectedScooterCondition;

    public CustomPane(Scooter scooter, AnchorPane paneParking, AnchorPane paneSelected, Label labelSelectionInfo,
                      Label labelSelectedScooterBatteryCharge, Label labelSelectedScooterModel, Label labelSelectedScooterCondition) {
        this.scooter = scooter;
        this.paneParking = paneParking;
        this.paneSelected = paneSelected;
        this.labelSelectionInfo = labelSelectionInfo;
        this.labelSelectedScooterBatteryCharge = labelSelectedScooterBatteryCharge;
        this.labelSelectedScooterModel = labelSelectedScooterModel;
        this.labelSelectedScooterCondition = labelSelectedScooterCondition;
    }

    public Parent show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("custom-pane-scooter.fxml"));
            loader.setControllerFactory(c ->
                    new CustomPaneController(scooter, paneParking, paneSelected, labelSelectionInfo,
                            labelSelectedScooterBatteryCharge, labelSelectedScooterModel, labelSelectedScooterCondition));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
