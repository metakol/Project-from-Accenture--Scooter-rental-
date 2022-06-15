package com.example.program;

import com.example.databases.*;
import com.example.dependencies.Scooter;
import com.example.dependencies.User;
import com.example.helpers.Animations;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminChangeScootersController implements Initializable {

    private User user;

    public AdminChangeScootersController(User user) {
        this.user = user;
    }

    @FXML
    void onClickGoBack(MouseEvent event) {
        Scenes.sceneChange(event, "admin-menu.fxml", new AdminMenuController(user));
    }

    @FXML
    private Label labelID;
    @FXML
    private Label labelSizeTable;
    @FXML
    private Label labelProcessForAdd;
    @FXML
    private Button buttonChange;

    @FXML
    private TableView<Scooter> table;
    @FXML
    private TableColumn<Scooter, Integer> columnID;
    @FXML
    private TableColumn<Scooter, String> columnModel;
    @FXML
    private TableColumn<Scooter, Integer> columnBatteryCharge;
    @FXML
    private TableColumn<Scooter, Integer> columnParkingSpace;
    @FXML
    private TableColumn<Scooter, String> columnCondition;

    ObservableList<Scooter> list = FXCollections.observableArrayList();
    ObservableList<String> modelsList = FXCollections.observableArrayList();
    ObservableList<Integer> parkingSpacesList = FXCollections.observableArrayList();
    ObservableList<String> conditionsList = FXCollections.observableArrayList();

    private Scooter selectItem = null;

    //для избежания ошибок, когда выделение поля в таблице пропадает
    private boolean wasUpdateTable = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        columnBatteryCharge.setCellValueFactory(new PropertyValueFactory<>("batteryCharge"));
        columnParkingSpace.setCellValueFactory(new PropertyValueFactory<>("parkingSpace"));
        columnCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
        table.setItems(list);

        TableView.TableViewSelectionModel<Scooter> selectionModel = table.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, oldItem, item) -> {
            if (!wasUpdateTable && list.size() > 0) {
                if (buttonChange.isDisable()) {
                    buttonChange.setDisable(false);
                }
                labelID.setText(String.valueOf(item.getID()));
                selectItem = item;
                modelForChange.setValue(item.getModel());
                parkingSpaceForChange.setValue(item.getParkingSpace());
                conditionForChange.setValue(item.getCondition());
                batteryChargeForChange.setValue(item.getBatteryCharge());
            } else {
                buttonChange.setDisable(true);
                labelID.setText("");
                selectItem = null;
            }
        });

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = "SELECT scooters.ID,modelname,IDparkingspace,batterycharge,condition " +
                        " FROM scooters" +
                        " INNER JOIN models ON IDmodel=models.ID" +
                        " INNER JOIN conditions ON IDcondition=conditions.ID";
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new Scooter(resultSet.getInt(ConstScootersDB.ID),
                                resultSet.getString(ConstModelsDB.MODEL_NAME),
                                resultSet.getInt(ConstScootersDB.ID_PARKING_SPACE),
                                resultSet.getInt(ConstScootersDB.BATTERY_CHARGE),
                                resultSet.getString(ConstConditionsDB.CONDITION)));
                    }
                }
                labelSizeTable.setText("Всего: " + list.size());

                query = SQLHelper.select(new String[]{ConstModelsDB.MODEL_NAME},
                        ConstModelsDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        modelsList.add(resultSet.getString(ConstModelsDB.MODEL_NAME));
                    }
                }

                query = SQLHelper.select(new String[]{ConstParkingSpacesDB.ID},
                        ConstParkingSpacesDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        parkingSpacesList.add(resultSet.getInt(ConstParkingSpacesDB.ID));
                    }
                }

                query = SQLHelper.select(new String[]{ConstConditionsDB.CONDITION},
                        ConstConditionsDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        conditionsList.add(resultSet.getString(ConstConditionsDB.CONDITION));
                    }
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
            }
            modelForAdd.setItems(modelsList);
            parkingSpaceForAdd.setItems(parkingSpacesList);
            conditionForAdd.setItems(conditionsList);

            modelForChange.setItems(modelsList);
            parkingSpaceForChange.setItems(parkingSpacesList);
            conditionForChange.setItems(conditionsList);
        }
    }

    @FXML
    private ComboBox<String> modelForAdd;
    @FXML
    private ComboBox<Integer> parkingSpaceForAdd;
    @FXML
    private ComboBox<String> conditionForAdd;
    @FXML
    private Slider batteryChargeForAdd;

    @FXML
    void onClickAdd() {
        if (modelForAdd.getValue() == null || parkingSpaceForAdd.getValue() == null || conditionForAdd.getValue() == null) {
            Animations.showControll(labelProcessForAdd,1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    int IDModel;
                    int IDCondition;

                    String query;

                    query = SQLHelper.select(new String[]{ConstModelsDB.ID},
                            ConstModelsDB.TABLE_NAME,
                            ConstModelsDB.MODEL_NAME + "='" + modelForAdd.getValue() + "'");
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        IDModel = resultSet.getInt(ConstModelsDB.ID);
                    }

                    query = SQLHelper.select(new String[]{ConstConditionsDB.ID},
                            ConstConditionsDB.TABLE_NAME,
                            ConstConditionsDB.CONDITION + "='" + conditionForAdd.getValue() + "'");
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        IDCondition = resultSet.getInt(ConstConditionsDB.ID);
                    }

                    query = SQLHelper.insert(ConstScootersDB.TABLE_NAME,
                            new String[]{
                                    ConstScootersDB.ID_MODEL,
                                    ConstScootersDB.BATTERY_CHARGE,
                                    ConstScootersDB.ID_PARKING_SPACE,
                                    ConstScootersDB.ID_CONDITION
                            },
                            new String[]{
                                    String.valueOf(IDModel),
                                    String.valueOf(batteryChargeForAdd.getValue()),
                                    String.valueOf(parkingSpaceForAdd.getValue()),
                                    String.valueOf(IDCondition)
                    });
                    statement.executeUpdate(query);

                    int lastID;
                    query = SQLHelper.selectMax(ConstScootersDB.ID,
                            "maxID",
                            ConstScootersDB.TABLE_NAME);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        lastID = resultSet.getInt("maxID");
                    }

                    list.add(new Scooter(lastID, modelForAdd.getValue(), parkingSpaceForAdd.getValue(), (int) batteryChargeForAdd.getValue(), conditionForAdd.getValue()));
                    labelSizeTable.setText("Всего: " + list.size());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                }
            } else labelProcessForAdd.setText("Ошибка подключения к БД");
        }
    }

    @FXML
    private ComboBox<String> modelForChange;
    @FXML
    private ComboBox<Integer> parkingSpaceForChange;
    @FXML
    private ComboBox<String> conditionForChange;
    @FXML
    private Slider batteryChargeForChange;

    @FXML
    void onClickChange() {
        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                int IDModel;
                int IDCondition;

                String query;

                query = SQLHelper.select(new String[]{ConstModelsDB.ID},
                        ConstModelsDB.TABLE_NAME,
                        ConstModelsDB.MODEL_NAME + "='" + modelForChange.getValue() + "'");
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    IDModel = resultSet.getInt(ConstModelsDB.ID);
                }

                query = SQLHelper.select(new String[]{ConstConditionsDB.ID},
                        ConstConditionsDB.TABLE_NAME,
                        ConstConditionsDB.CONDITION + "='" + conditionForChange.getValue() + "'");
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    IDCondition = resultSet.getInt(ConstConditionsDB.ID);
                }

                query = SQLHelper.update(ConstScootersDB.TABLE_NAME,
                        new String[]{ConstScootersDB.ID_MODEL + "='" + IDModel + "'",
                                ConstScootersDB.BATTERY_CHARGE + "='" + (int) batteryChargeForChange.getValue() + "'",
                                ConstScootersDB.ID_PARKING_SPACE + "='" + parkingSpaceForChange.getValue() + "'",
                                ConstScootersDB.ID_CONDITION + "='" + IDCondition + "'"},
                        ConstScootersDB.ID + "='" + selectItem.getID() + "'");
                statement.executeUpdate(query);

                wasUpdateTable = true;

                list.set(table.getSelectionModel().getSelectedIndex(),
                        new Scooter(list.get(table.getSelectionModel().getSelectedIndex()).getID(), modelForChange.getValue(), parkingSpaceForChange.getValue(), (int) batteryChargeForChange.getValue(), conditionForChange.getValue()));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
                wasUpdateTable = false;
            }
        }
    }

    @FXML
    void onClickRemove() {
        if (selectItem != null) {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    String query = SQLHelper.delete(ConstScootersDB.TABLE_NAME,
                            ConstScootersDB.ID + "='" + selectItem.getID() + "'");
                    statement.executeUpdate(query);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                }
            }
            System.out.println(list.size());
            if (table.getSelectionModel().getSelectedIndex() == 0) {
                List<Scooter> bufList = new ArrayList<>();
                for (int i = 1; i < list.size(); i++) {
                    bufList.add(list.get(i));
                }
                list.clear();
                list.addAll(bufList);
            } else {
                list.removeIf(s -> s.getID() == selectItem.getID());
            }
            labelSizeTable.setText("Всего: " + list.size());
        }
    }
}
