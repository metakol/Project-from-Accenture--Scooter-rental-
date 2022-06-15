package com.example.program;

import com.example.dependencies.Scooter;
import com.example.helpers.Animations;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class CustomPaneController implements Initializable {

    @FXML
    private Label labelID;

    @FXML
    private Label labelModel;

    @FXML
    private Label labelBatteryCharge;

    @FXML
    private Label labelScooterCondition;

    @FXML
    private ImageView imageScooter;

    @FXML
    private Label labelApproximateDistance;


    @FXML
    void onSelect(MouseEvent event) {
       // AnchorPane.setRightAnchor(paneSelected, 0.);
        Animations.showSlideRight(paneSelected);

        labelSelectedInfo.setText("Самокат № " + scooter.getID() + " с парковки №" + scooter.getParkingSpace());
        labelSelectedScooterBatteryCharge.setText(scooter.getBatteryCharge()+"%");
        labelSelectedScooterModel.setText(scooter.getModel());
        labelSelectedScooterCondition.setText("Сост.: "+scooter.getCondition());
        paneSelected.setDisable(false);
        paneParking.setDisable(true);
    }

    private Scooter scooter;
    private AnchorPane paneSelected;
    private AnchorPane paneParking;
    private Label labelSelectedInfo;
    private Label labelSelectedScooterBatteryCharge;
    private Label labelSelectedScooterModel;
    private Label labelSelectedScooterCondition;


    public CustomPaneController(Scooter scooter, AnchorPane paneParking, AnchorPane paneSelected, Label labelSelectedInfo,
                                Label labelSelectedScooterBatteryCharge,Label labelSelectedScooterModel,Label labelSelectedScooterCondition) {
        this.scooter = scooter;
        this.paneParking = paneParking;
        this.paneSelected = paneSelected;
        this.labelSelectedInfo = labelSelectedInfo;
        this.labelSelectedScooterBatteryCharge=labelSelectedScooterBatteryCharge;
        this.labelSelectedScooterModel=labelSelectedScooterModel;
        this.labelSelectedScooterCondition=labelSelectedScooterCondition;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelID.setText("#" + scooter.getID());
        labelModel.setText(scooter.getModel());
        labelBatteryCharge.setText(scooter.getBatteryCharge() + "%");
        InputStream inputStream = getClass().getResourceAsStream("/com/example/imageScooters/" + scooter.getImageName());
        imageScooter.setImage(new Image(inputStream));
        labelScooterCondition.setText("Сост. " + scooter.getCondition());
        labelApproximateDistance.setText(40 * scooter.getBatteryCharge() / 100 + 1 + " км");
    }
}
