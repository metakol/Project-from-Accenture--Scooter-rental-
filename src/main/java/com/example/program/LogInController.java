package com.example.program;

import com.example.databases.ConstUsersDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.User;
import com.example.helpers.Animations;
import com.example.helpers.Fields;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class LogInController {
    @FXML
    private Label labelProcess;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField textFieldPass;

    @FXML
    void onClickLogIn(MouseEvent event) {
        if (Fields.fieldsAreEmpty(textFieldEmail, textFieldPass)) {
            labelProcess.setText("Заполните все поля!");
            Animations.showControll(labelProcess,1000);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                if (logIn(textFieldEmail.getText(), textFieldPass.getText(), handler)) {
                    labelProcess.setText("Успешно! Секунду..");
                    Animations.showControll(labelProcess,3000,"#83f183");
                    Scenes.sceneChange(event, "main-scene.fxml", new MainSceneController(getUser(textFieldEmail.getText(), handler)));
                } else {
                    labelProcess.setText("Почта или пароль не верны!");
                    Animations.showControll(labelProcess,1500);
                }
                handler.close();
            }
        }

    }

    @FXML
    void onClickSignUp(MouseEvent event) {
        Scenes.sceneChange(event, "sign-up.fxml");
    }
    private boolean logIn(String inputEmail, String inputPass, DatabaseHandler handler) {
        boolean dataIsTrue = false;
        try (Statement statement = handler.createStatement()) {
            String query = SQLHelper.select(
                    new String[]{ConstUsersDB.EMAIL, ConstUsersDB.PASSWORD},
                    ConstUsersDB.TABLE_NAME,
                    ConstUsersDB.EMAIL + " = '" + inputEmail + "'");
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    dataIsTrue = inputPass.equals(resultSet.getString(ConstUsersDB.PASSWORD));
                }
            } catch (SQLException e) {
                System.out.println("\tError with ResultSet!");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("\tError with Statement");
            e.printStackTrace();
        }
        return dataIsTrue;
    }

    private User getUser(String emailUser, DatabaseHandler handler) {
        try (Statement statement = handler.createStatement()) {
            String query = SQLHelper.select(new String[]{
                            ConstUsersDB.ID, ConstUsersDB.USERNAME,
                            ConstUsersDB.EMAIL, ConstUsersDB.PASSWORD,
                            ConstUsersDB.PHONENUMBER, ConstUsersDB.BALANCE,
                            ConstUsersDB.IS_ADMIN},
                    ConstUsersDB.TABLE_NAME,
                    ConstUsersDB.EMAIL + " = '" + emailUser + "'"
            );
            try (ResultSet resultSet = statement.executeQuery(query)) {
                return new User(
                        resultSet.getInt(ConstUsersDB.ID), resultSet.getString(ConstUsersDB.USERNAME), resultSet.getString(ConstUsersDB.EMAIL),
                        resultSet.getString(ConstUsersDB.PASSWORD), resultSet.getString(ConstUsersDB.PHONENUMBER), resultSet.getInt(ConstUsersDB.BALANCE),
                        resultSet.getBoolean(ConstUsersDB.IS_ADMIN)
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
