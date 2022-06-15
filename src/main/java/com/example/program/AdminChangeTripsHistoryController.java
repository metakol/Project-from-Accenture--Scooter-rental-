package com.example.program;

import com.example.databases.ConstTripsHistoryDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.TripHistory;
import com.example.dependencies.User;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminChangeTripsHistoryController implements Initializable {

    private User user;

    public AdminChangeTripsHistoryController(User user) {
        this.user = user;
    }

    @FXML
    void onClickGoBack(MouseEvent event) {
        Scenes.sceneChange(event, "admin-menu.fxml", new AdminMenuController(user));
    }

    @FXML
    private TableView<TripHistory> table;
    @FXML
    private TableColumn<TripHistory, Integer> columnID;
    @FXML
    private TableColumn<TripHistory, Integer> columnUserID;
    @FXML
    private TableColumn<TripHistory, Integer> columnScooterID;
    @FXML
    private TableColumn<TripHistory, Integer> columnTariffID;
    @FXML
    private TableColumn<TripHistory, Boolean> columnIsFinished;
    @FXML
    private TableColumn<TripHistory, Integer> columnCost;
    @FXML
    private TableColumn<TripHistory, Integer> columnStartParkingID;
    @FXML
    private TableColumn<TripHistory, Integer> columnFinishParkingID;
    @FXML
    private TableColumn<TripHistory, String> columnTimeStart;
    @FXML
    private TableColumn<TripHistory, String> columnTimeFinish;

    ObservableList<TripHistory> list = FXCollections.observableArrayList();

    @FXML
    private Label labelSizeTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnUserID.setCellValueFactory(new PropertyValueFactory<>("IDUser"));
        columnScooterID.setCellValueFactory(new PropertyValueFactory<>("IDScooter"));
        columnTariffID.setCellValueFactory(new PropertyValueFactory<>("IDTariff"));
        columnIsFinished.setCellValueFactory(new PropertyValueFactory<>("isFinished"));
        columnCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        columnStartParkingID.setCellValueFactory(new PropertyValueFactory<>("IDParkingStart"));
        columnFinishParkingID.setCellValueFactory(new PropertyValueFactory<>("IDParkingFinish"));
        columnTimeStart.setCellValueFactory(new PropertyValueFactory<>("timeStart"));
        columnTimeFinish.setCellValueFactory(new PropertyValueFactory<>("timeFinish"));

        table.setItems(list);

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{"*"},
                        ConstTripsHistoryDB.TABLE_NAME);
                System.out.println(query);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new TripHistory(
                                resultSet.getInt(ConstTripsHistoryDB.ID),
                                resultSet.getInt(ConstTripsHistoryDB.ID_USER),
                                resultSet.getInt(ConstTripsHistoryDB.ID_SCOOTER),
                                resultSet.getInt(ConstTripsHistoryDB.ID_TARIFF),
                                resultSet.getBoolean(ConstTripsHistoryDB.IS_FINISHED),
                                resultSet.getInt(ConstTripsHistoryDB.COST),
                                resultSet.getInt(ConstTripsHistoryDB.ID_PARKING_START),
                                resultSet.getInt(ConstTripsHistoryDB.ID_PARKING_FINISH),
                                resultSet.getString(ConstTripsHistoryDB.TIME_START),
                                resultSet.getString(ConstTripsHistoryDB.TIME_FINISH)));
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
