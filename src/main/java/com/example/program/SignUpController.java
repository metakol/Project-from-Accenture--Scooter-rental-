package com.example.program;

import com.example.databases.ConstUsersDB;
import com.example.databases.DatabaseHandler;
import com.example.helpers.Animations;
import com.example.helpers.Fields;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpController {
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField textFieldPass;
    @FXML
    private TextField textFieldNumber;
    @FXML
    private Label labelProcess;

    @FXML
    void onClickGoBack(MouseEvent event) {
        Scenes.sceneChange(event, "log-in.fxml");
    }

    @FXML
    void onClickSignUp(MouseEvent event) {
        int maxLength = 20;
        if (Fields.fieldsAreEmpty(textFieldUsername, textFieldEmail, textFieldPass, textFieldNumber)) {
            labelProcess.setText("Все поля должны быть заполненны");
        } else if (textFieldUsername.getLength() >= maxLength) {
            labelProcess.setText("Логин должен быть меньше " + maxLength + " символов!");
        } else if (textFieldPass.getLength() >= maxLength) {
            labelProcess.setText("Пароль должен быть меньше " + 20 + " символов!");
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                signUp(textFieldUsername.getText(), textFieldEmail.getText(), textFieldPass.getText(), textFieldNumber.getText(), handler);
                handler.close();
            }
        }
        Animations.showControll(labelProcess, 3000);
    }

    private void signUp(String username, String email, String password, String phoneNumber, DatabaseHandler handler) {
        boolean result = true;
        try (Statement statement = handler.createStatement()) {

            String query = SQLHelper.select(
                    new String[]{
                            ConstUsersDB.EMAIL,
                            ConstUsersDB.USERNAME,
                            ConstUsersDB.PHONENUMBER
                    },
                    ConstUsersDB.TABLE_NAME,
                    ConstUsersDB.EMAIL + " = '" + email +
                            "' OR " + ConstUsersDB.USERNAME + " = '" + username +
                            "' OR " + ConstUsersDB.PHONENUMBER + " = '" + phoneNumber + "'");
            try (ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    if (username.equals(resultSet.getString(ConstUsersDB.USERNAME)))
                        labelProcess.setText("Аккаунт с таким ником уже существует!");
                    else if (email.equals(resultSet.getString(ConstUsersDB.EMAIL)))
                        labelProcess.setText("Аккаунт с таким email уже существует!");
                    else if (phoneNumber.equals(resultSet.getString(ConstUsersDB.PHONENUMBER)))
                        labelProcess.setText("Аккаунт с таким номером уже существует!");
                    result = false;
                }
            } catch (SQLException e) {
                System.out.println("\tError with ResultSet!");
                e.printStackTrace();
            }
            if (result) {
                labelProcess.setText("Аккаунт успешно создан!");

                query = SQLHelper.insert(ConstUsersDB.TABLE_NAME,
                        new String[]{ConstUsersDB.USERNAME, ConstUsersDB.EMAIL,
                                ConstUsersDB.PASSWORD, ConstUsersDB.PHONENUMBER,
                                ConstUsersDB.BALANCE, ConstUsersDB.IS_ADMIN},
                        new String[]{username, email, password, phoneNumber, "0", "0"});

                statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            System.out.println("\tError with Statement");
            e.printStackTrace();
        }
    }

}
