package com.example.program;

import com.example.databases.ConstModelsDB;
import com.example.databases.ConstScootersDB;
import com.example.databases.ConstTariffsDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.Tariff;
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
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class AdminChangeTariffsController implements Initializable {

    private User user;

    public AdminChangeTariffsController(User user) {
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
    private Button buttonChange;

    @FXML
    private TableView<Tariff> table;
    @FXML
    private TableColumn<Tariff, Integer> columnID;
    @FXML
    private TableColumn<Tariff, Integer> columnCostForMinute;
    @FXML
    private TableColumn<Tariff, Integer> columnMaxSpeed;

    ObservableList<Tariff> list = FXCollections.observableArrayList();

    private Tariff selectItem = null;

    //для избежания ошибок, когда выделение поля в таблице пропадает
    private boolean wasUpdateTable = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnCostForMinute.setCellValueFactory(new PropertyValueFactory<>("costForMinute"));
        columnMaxSpeed.setCellValueFactory(new PropertyValueFactory<>("maxSpeed"));
        table.setItems(list);
        TableView.TableViewSelectionModel<Tariff> selectionModel = table.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, oldItem, item) -> {
            if (!wasUpdateTable && list.size() > 0) {
                if (buttonChange.isDisable()) {
                    buttonChange.setDisable(false);
                }
                labelID.setText(String.valueOf(item.getID()));
                selectItem = item;
                costForMinuteForChange.setText(String.valueOf(item.getCostForMinute()));
                maxSpeedForChange.setText(String.valueOf(item.getMaxSpeed()));
            } else {
                buttonChange.setDisable(true);
                labelID.setText("");
                selectItem = null;
            }
        });

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{ConstTariffsDB.ID, ConstTariffsDB.COST_FOR_MINUTE, ConstTariffsDB.MAX_SPEED},
                        ConstTariffsDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new Tariff(resultSet.getInt(ConstTariffsDB.ID),
                                resultSet.getInt(ConstTariffsDB.COST_FOR_MINUTE),
                                resultSet.getInt(ConstTariffsDB.MAX_SPEED)));
                    }
                }
                labelSizeTable.setText("Всего: " + list.size());

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
            }
        }
//
//        UnaryOperator<TextFormatter.Change> filter = change -> {
//            String text = change.getText();
//            if (text.matches("[0-9]*")) {
//                return change;
//            }
//            return null;
//        };
//        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
//        costForMinuteForAdd.setTextFormatter(textFormatter);
////        costForMinuteForChange.setTextFormatter(textFormatter);
//        maxSpeedForAdd.setTextFormatter(textFormatter);
//        maxSpeedForChange.setText
////        maxSpeedForChange.setTextFormatter(textFormatter);

    }

    @FXML
    private TextField costForMinuteForAdd;
    @FXML
    private TextField maxSpeedForAdd;

    @FXML
    void onClickAdd() {
        if (Fields.fieldsAreEmpty(costForMinuteForAdd, maxSpeedForAdd)) {
            Animations.showControll(labelProcessForAdd, 1500);
        } else if (!Fields.containsOnlyDigits(costForMinuteForAdd, maxSpeedForAdd)) {
            Animations.showControll(labelProcessForAdd, 1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    String query;

                    query = SQLHelper.insert(ConstTariffsDB.TABLE_NAME,
                            new String[]{ConstTariffsDB.COST_FOR_MINUTE, ConstTariffsDB.MAX_SPEED},
                            new String[]{costForMinuteForAdd.getText(), maxSpeedForAdd.getText()});
                    statement.executeUpdate(query);

                    int lastID;
                    query = SQLHelper.selectMax(ConstTariffsDB.ID,
                            "maxID",
                            ConstTariffsDB.TABLE_NAME);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        lastID = resultSet.getInt("maxID");
                    }

                    list.add(new Tariff(lastID,
                            Integer.parseInt(costForMinuteForAdd.getText()),
                            Integer.parseInt(maxSpeedForAdd.getText())));
                    Fields.clear(costForMinuteForAdd, maxSpeedForAdd);
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
    private TextField costForMinuteForChange;
    @FXML
    private TextField maxSpeedForChange;

    @FXML
    void onClickChange() {
        if (Fields.fieldsAreEmpty(costForMinuteForChange, maxSpeedForChange)) {
            Animations.showControll(labelProcessForChange, 1500);
        } else if (!Fields.containsOnlyDigits(costForMinuteForChange, maxSpeedForChange)) {
            Animations.showControll(labelProcessForChange, 1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {

                    String query;

                    query = SQLHelper.update(ConstTariffsDB.TABLE_NAME,
                            new String[]{
                                    ConstTariffsDB.COST_FOR_MINUTE + "='" + costForMinuteForChange.getText() + "'",
                                    ConstTariffsDB.MAX_SPEED + "='" + maxSpeedForChange.getText() + "'"
                            },
                            ConstTariffsDB.ID + "='" + selectItem.getID() + "'");
                    statement.executeUpdate(query);

                    wasUpdateTable = true;

                    list.set(table.getSelectionModel().getSelectedIndex(),
                            new Tariff(list.get(table.getSelectionModel().getSelectedIndex()).getID(),
                                    Integer.parseInt(costForMinuteForChange.getText()),
                                    Integer.parseInt(maxSpeedForChange.getText())));
                    Fields.clear(costForMinuteForChange, maxSpeedForChange);
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
                    String query = SQLHelper.delete(ConstTariffsDB.TABLE_NAME,
                            ConstTariffsDB.ID + "='" + selectItem.getID() + "'");
                    statement.executeUpdate(query);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                }
            }
            if (table.getSelectionModel().getSelectedIndex() == 0) {
                List<Tariff> bufList = new ArrayList<>();
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
