package com.example.program;

import com.example.databases.ConstConditionsDB;
import com.example.databases.ConstScootersDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.Condition;
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

public class AdminChangeConditionsController implements Initializable {

    private User user;

    public AdminChangeConditionsController(User user) {
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
    private Label labelProcessForRemove;
    @FXML
    private Button buttonChange;

    @FXML
    private TableView<Condition> table;
    @FXML
    private TableColumn<Condition, Integer> columnID;
    @FXML
    private TableColumn<Condition, String> columnCondition;

    ObservableList<Condition> list = FXCollections.observableArrayList();

    private Condition selectItem = null;

    //для избежания ошибок, когда выделение поля в таблице пропадает
    private boolean wasUpdateTable = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnCondition.setCellValueFactory(new PropertyValueFactory<>("condition"));
        table.setItems(list);

        TableView.TableViewSelectionModel<Condition> selectionModel = table.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, oldItem, item) -> {
            if (!wasUpdateTable && list.size() > 0) {
                if (buttonChange.isDisable()) {
                    buttonChange.setDisable(false);
                }
                labelID.setText(String.valueOf(item.getID()));
                selectItem = item;
                conditionForChange.setText(item.getCondition());
            } else {
                buttonChange.setDisable(true);
                labelID.setText("");
                selectItem = null;
            }
        });

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{ConstConditionsDB.ID, ConstConditionsDB.CONDITION},
                        ConstConditionsDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new Condition(resultSet.getInt(ConstConditionsDB.ID), resultSet.getString(ConstConditionsDB.CONDITION)));
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
    private TextField conditionForAdd;

    @FXML
    void onClickAdd() {
        if (Fields.fieldsAreEmpty(conditionForAdd)) {
            Animations.showControll(labelProcessForAdd,1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {

                    String query;

                    query = SQLHelper.insert(ConstConditionsDB.TABLE_NAME,
                            new String[]{ConstConditionsDB.CONDITION},
                            new String[]{conditionForAdd.getText()});
                    statement.executeUpdate(query);

                    int lastID;
                    query = SQLHelper.selectMax(ConstConditionsDB.ID,
                            "maxID",
                            ConstConditionsDB.TABLE_NAME);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        lastID = resultSet.getInt("maxID");
                    }

                    list.add(new Condition(lastID, conditionForAdd.getText()));
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
    private TextField conditionForChange;

    @FXML
    void onClickChange() {
        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {

                String query;

                query = SQLHelper.update(ConstConditionsDB.TABLE_NAME,
                        new String[]{ConstConditionsDB.CONDITION + "='" + conditionForChange.getText() + "'"},
                        ConstConditionsDB.ID + "='" + selectItem.getID() + "'");
                statement.executeUpdate(query);

                wasUpdateTable = true;

                list.set(table.getSelectionModel().getSelectedIndex(),
                        new Condition(list.get(table.getSelectionModel().getSelectedIndex()).getID(), conditionForChange.getText()));
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

                    String query = SQLHelper.select(new String[]{ConstScootersDB.ID},
                            ConstScootersDB.TABLE_NAME,
                            ConstScootersDB.ID_CONDITION + "='" + selectItem.getID() + "'");

                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        if (resultSet.next()) {
                            Animations.showControll(labelProcessForRemove,3000);
                        } else {
                            query = SQLHelper.delete(ConstConditionsDB.TABLE_NAME,
                                    ConstConditionsDB.ID + "='" + selectItem.getID() + "'");
                            statement.executeUpdate(query);

                            if (table.getSelectionModel().getSelectedIndex() == 0) {
                                List<Condition> bufList = new ArrayList<>();
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
