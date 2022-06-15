package com.example.program;

import com.example.dependencies.User;
import com.example.helpers.Scenes;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class AdminMenuController {
    private User user;

    public AdminMenuController(User user) {
        this.user = user;
    }

    @FXML
    void onClickGoBack(MouseEvent event) {
        Scenes.sceneChange(event,"main-scene.fxml",new MainSceneController(user));
    }

    @FXML
    void onClickGoScooters(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-scooters.fxml",new AdminChangeScootersController(user));
    }

    @FXML
    void onClickGoCondition(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-conditions.fxml",new AdminChangeConditionsController(user));
    }

    @FXML
    void onClickGoModels(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-models.fxml",new AdminChangeModelsController(user));
    }

    @FXML
    void onClickGoUsers(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-users.fxml",new AdminChangeUsersController(user));
    }
    @FXML
    void onClickGoParkingSpaces(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-parkingspaces.fxml",new AdminChangeParkingSpacesController(user));
    }

    @FXML
    void onClickGoTariffs(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-tariffs.fxml",new AdminChangeTariffsController (user));
    }

    @FXML
    void onClickGoTripHistory(MouseEvent event) {
        Scenes.sceneChange(event,"admin-change-tripshistory.fxml",new AdminChangeTripsHistoryController(user));
    }

}
