package com.example.program;

import com.example.databases.ConstConditionsDB;
import com.example.databases.ConstModelsDB;
import com.example.databases.ConstScootersDB;
import com.example.databases.DatabaseHandler;
import com.example.dependencies.Model;
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

public class AdminChangeModelsController implements Initializable {

    private User user;

    public AdminChangeModelsController(User user) {
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
    private TableView<Model> table;
    @FXML
    private TableColumn<Model, Integer> columnID;
    @FXML
    private TableColumn<Model, String> columnModel;
    @FXML
    private TableColumn<Model, String> columnImage;

    ObservableList<Model> list = FXCollections.observableArrayList();

    private Model selectItem = null;

    //для избежания ошибок, когда выделение поля в таблице пропадает
    private boolean wasUpdateTable = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("modelName"));
        columnImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        table.setItems(list);
        TableView.TableViewSelectionModel<Model> selectionModel = table.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, oldItem, item) -> {
            if (!wasUpdateTable && list.size() > 0) {
                if (buttonChange.isDisable()) {
                    buttonChange.setDisable(false);
                }
                labelID.setText(String.valueOf(item.getID()));
                selectItem = item;
                modelForChange.setText(String.valueOf(item.getModelName()));
                imageForChange.setText(String.valueOf(item.getImage()));
            } else {
                buttonChange.setDisable(true);
                labelID.setText("");
                selectItem = null;
            }
        });

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(new String[]{
                                ConstModelsDB.ID, ConstModelsDB.MODEL_NAME, ConstModelsDB.IMAGE
                        },
                        ConstModelsDB.TABLE_NAME);
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        list.add(new Model(resultSet.getInt(ConstModelsDB.ID),
                                resultSet.getString(ConstModelsDB.MODEL_NAME),
                                resultSet.getString(ConstModelsDB.IMAGE)));
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
    private TextField modelForAdd;
    @FXML
    private TextField imageForAdd;

    @FXML
    void onClickAdd() {
        if (Fields.fieldsAreEmpty(modelForAdd, imageForAdd)) {
            Animations.showControll(labelProcessForAdd,1500);
        } else {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    String query;

                    query = SQLHelper.insert(ConstModelsDB.TABLE_NAME,
                            new String[]{ConstModelsDB.MODEL_NAME, ConstModelsDB.IMAGE},
                            new String[]{modelForAdd.getText(), imageForAdd.getText()});
                    statement.executeUpdate(query);

                    int lastID;
                    query = SQLHelper.selectMax(ConstModelsDB.ID,
                            "maxID",
                            ConstModelsDB.TABLE_NAME);
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        lastID = resultSet.getInt("maxID");
                    }

                    list.add(new Model(lastID, modelForAdd.getText(), imageForAdd.getText()));
                    Fields.clear(modelForAdd, imageForAdd);
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
    private TextField modelForChange;
    @FXML
    private TextField imageForChange;

    @FXML
    void onClickChange() {
        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {

                String query;

                query = SQLHelper.update(ConstModelsDB.TABLE_NAME,
                        new String[]{
                                ConstModelsDB.MODEL_NAME + "='" + modelForChange.getText() + "'",
                                ConstModelsDB.IMAGE + "='" + imageForChange.getText() + "'"
                        },
                        ConstModelsDB.ID + "='" + selectItem.getID() + "'");

                statement.executeUpdate(query);

                wasUpdateTable = true;

                list.set(table.getSelectionModel().getSelectedIndex(),
                        new Model(list.get(table.getSelectionModel().getSelectedIndex()).getID(),
                                modelForChange.getText(),
                                imageForChange.getText()));
                Fields.clear(modelForChange, imageForChange);
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

                            query = SQLHelper.delete(ConstModelsDB.TABLE_NAME,
                                    ConstModelsDB.ID + "='" + selectItem.getID() + "'");
                            statement.executeUpdate(query);

                            if (table.getSelectionModel().getSelectedIndex() == 0) {
                                List<Model> bufList = new ArrayList<>();
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




