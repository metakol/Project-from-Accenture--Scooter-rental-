package com.example.program;

import com.example.databases.DatabaseHandler;
import com.example.dependencies.User;
import com.example.helpers.Animations;
import com.example.helpers.Fields;
import com.example.helpers.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.BufferedReader;

public class MenuController {

    @FXML
    private TextField textFieldBankCard;
    @FXML
    private TextField textFieldCVV;
    @FXML
    private TextField textFieldSum;
    @FXML
    private Label labelProcess;

    private User user;

    public MenuController(User user) {
        System.out.println("Конструктор MenuController");
        this.user = user;
    }

    @FXML
    void onClickTopUp(MouseEvent event) {
        labelProcess.setStyle("-fx-text-fill: #dc0505");
        if (Fields.fieldsAreEmpty(textFieldBankCard, textFieldSum, textFieldCVV)) {
            labelProcess.setText("Не все поля заполнены!");
        } else if(!Fields.containsOnlyDigits(textFieldBankCard, textFieldSum, textFieldCVV)){
            labelProcess.setText("Данные не коректны!");
        } else {
            if (user.changeBalance(textFieldBankCard.getText(), textFieldCVV.getText(), Integer.parseInt(textFieldSum.getText()))) {
                labelProcess.setStyle("-fx-text-fill: linear-gradient(to left, #87ff9f, #24ff50)");
                labelProcess.setText("Успешное пополнение!");
                Fields.clear(textFieldBankCard,textFieldCVV,textFieldSum);
            } else {
                labelProcess.setText("Данные не коректны!");
            }
        }
        Animations.showControll(labelProcess, 2000);
    }

    @FXML
    void onClickGoBack(MouseEvent event) {
        System.out.println("Переход на main-scene");
        Scenes.sceneChange(event, "main-scene.fxml", new MainSceneController(user));
    }

    @FXML
    void onClickLeave(MouseEvent event) {
        Scenes.sceneChange(event, "log-in.fxml");
    }


}
