package com.example.program;

import com.example.databases.ConstUsersDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.User;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminChangeUsersController implements Initializable {

    private User user;

    public AdminChangeUsersController(User user) {
        this.user = user;
    }

    @FXML
    void onClickGoBack(MouseEvent event) {
        Scenes.sceneChange(event, "admin-menu.fxml", new AdminMenuController(user));
    }

    @FXML
    private TableView<User> table;
    @FXML
    private TableColumn<User, Integer> columnID;
    @FXML
    private TableColumn<User, String> columnUsername;
    @FXML
    private TableColumn<User, String> columnEmail;
    @FXML
    private TableColumn<User, String> columnPhone;
    @FXML
    private TableColumn<User, Integer> columnBalance;
    @FXML
    private TableColumn<User, Boolean> columnIsAdmin;

    ObservableList<User> list = FXCollections.observableArrayList();

    @FXML
    private Label labelSizeTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        columnBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        columnIsAdmin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        table.setItems(list);

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{ConstUsersDB.ID,ConstUsersDB.USERNAME,ConstUsersDB.EMAIL,ConstUsersDB.PHONENUMBER,ConstUsersDB.BALANCE,ConstUsersDB.IS_ADMIN},
                        ConstUsersDB.TABLE_NAME);
                System.out.println(query);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new User(resultSet.getInt(ConstUsersDB.ID),resultSet.getString(ConstUsersDB.USERNAME),resultSet.getString(ConstUsersDB.EMAIL),resultSet.getString(ConstUsersDB.PHONENUMBER),resultSet.getInt(ConstUsersDB.BALANCE),resultSet.getBoolean(ConstUsersDB.IS_ADMIN)));
                        System.out.println(resultSet.getBoolean(ConstUsersDB.IS_ADMIN));
                    }
                }
                labelSizeTable.setText("Всего: " + list.size());

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
            }
        }
    }

}
