package com.example.program;

import com.example.databases.*;
import com.example.dependencies.ParkingSpace;
import com.example.dependencies.User;
import com.example.helpers.Animations;
import com.example.helpers.Fields;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class AdminChangeParkingSpacesController implements Initializable {

    private User user;

    public AdminChangeParkingSpacesController(User user) {
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
    private Label labelProcessForChange;
    @FXML
    private Label labelProcessForRemove;
    @FXML
    private Button buttonChange;

    @FXML
    private TableView<ParkingSpace> table;
    @FXML
    private TableColumn<ParkingSpace, Integer> columnID;
    @FXML
    private TableColumn<ParkingSpace, String> columnGeoposition;
    @FXML
    private TableColumn<ParkingSpace, Float> columnRadius;

    ObservableList<ParkingSpace> list = FXCollections.observableArrayList();

    private ParkingSpace selectItem = null;

    //для избежания ошибок, когда выделение поля в таблице пропадает
    private boolean wasUpdateTable = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnGeoposition.setCellValueFactory(new PropertyValueFactory<>("geoposition"));
        columnRadius.setCellValueFactory(new PropertyValueFactory<>("radius"));
        table.setItems(list);
        TableView.TableViewSelectionModel<ParkingSpace> selectionModel = table.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, oldItem, item) -> {
            if (!wasUpdateTable && list.size() > 0) {
                if (buttonChange.isDisable()) {
                    buttonChange.setDisable(false);
                }
                labelID.setText(String.valueOf(item.getID()));
                selectItem = item;
                geopositionForChange.setText(String.valueOf(item.getGeoposition()));
                radiusForChange.setText(String.valueOf(item.getRadius()));
            } else {
                buttonChange.setDisable(true);
                labelID.setText("");
                selectItem = null;
            }
        });

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{ConstParkingSpacesDB.ID, ConstParkingSpacesDB.GEOPOSITION, ConstParkingSpacesDB.RADIUS},
                        ConstParkingSpacesDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        if (resultSet.getInt(ConstTariffsDB.ID) == 0) continue;

                        list.add(new ParkingSpace(resultSet.getInt(ConstParkingSpacesDB.ID),
                                resultSet.getString(ConstParkingSpacesDB.GEOPOSITION),
                                resultSet.getFloat(ConstParkingSpacesDB.RADIUS)));

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

    @FXML
    private TextField geopositionForAdd;
    @FXML
    private TextField radiusForAdd;

    @FXML
    void onClickAdd() {
        if (Fields.fieldsAreEmpty(geopositionForAdd, radiusForAdd)) {
            Animations.showControll(labelProcessForAdd, 1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    String query;

                    query = SQLHelper.insert(ConstParkingSpacesDB.TABLE_NAME,
                            new String[]{ConstParkingSpacesDB.GEOPOSITION, ConstParkingSpacesDB.RADIUS},
                            new String[]{geopositionForAdd.getText(), radiusForAdd.getText()});
                    statement.executeUpdate(query);

                    int lastID;
                    query = SQLHelper.selectMax(ConstParkingSpacesDB.ID,
                            "maxID",
                            ConstParkingSpacesDB.TABLE_NAME);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        lastID = resultSet.getInt("maxID");
                    }

                    list.add(new ParkingSpace(lastID,
                            geopositionForAdd.getText(),
                            Float.parseFloat(radiusForAdd.getText())));
                    Fields.clear(geopositionForAdd, radiusForAdd);
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
    private TextField geopositionForChange;
    @FXML
    private TextField radiusForChange;

    @FXML
    void onClickChange() {
        if (Fields.fieldsAreEmpty(geopositionForChange, radiusForChange)) {
            Animations.showControll(labelProcessForChange, 1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {

                    String query;

                    query = SQLHelper.update(ConstParkingSpacesDB.TABLE_NAME,
                            new String[]{
                                    ConstParkingSpacesDB.GEOPOSITION + "='" + geopositionForChange.getText() + "'",
                                    ConstParkingSpacesDB.RADIUS + "='" + radiusForChange.getText() + "'"
                            },
                            ConstParkingSpacesDB.ID + "='" + selectItem.getID() + "'");

                    statement.executeUpdate(query);

                    wasUpdateTable = true;

                    list.set(table.getSelectionModel().getSelectedIndex(),
                            new ParkingSpace(list.get(table.getSelectionModel().getSelectedIndex()).getID(),
                                    geopositionForChange.getText(),
                                    Float.parseFloat(radiusForChange.getText())));

                    Fields.clear(geopositionForChange, radiusForChange);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                    wasUpdateTable = false;
                }
            }
        }
    }

    @FXML
    void onClickRemove() {
        if (selectItem != null) {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {

                    String query = SQLHelper.select(new String[]{ConstScootersDB.ID},
                            ConstScootersDB.TABLE_NAME,
                            ConstScootersDB.ID_CONDITION + "='" + selectItem.getID() + "'");

                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        if (resultSet.next()) {
                            Animations.showControll(labelProcessForRemove, 3000);
                        } else {
                            query = SQLHelper.delete(ConstParkingSpacesDB.TABLE_NAME,
                                    ConstParkingSpacesDB.ID + "='" + selectItem.getID() + "'");
                            statement.executeUpdate(query);

                            if (table.getSelectionModel().getSelectedIndex() == 0) {
                                List<ParkingSpace> bufList = new ArrayList<>();
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
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                }

            }
        }
    }

}
