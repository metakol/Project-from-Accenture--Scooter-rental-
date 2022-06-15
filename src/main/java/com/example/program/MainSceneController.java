package com.example.program;

import com.example.databases.*;
import com.example.dependencies.Scooter;
import com.example.dependencies.Tariff;
import com.example.dependencies.User;
import com.example.helpers.Animations;
import com.example.helpers.SQLHelper;
import com.example.helpers.Scenes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.Date;

public class MainSceneController implements Initializable {
    private User user;

    public MainSceneController(User user) {
        System.out.println("Конструктор MainScene");
        this.user = user;
    }

    @FXML
    private Button labelBalance;
    @FXML
    private Button buttonMenu;

    @FXML
    private Button buttonForAdmin;
    @FXML
    public WebView browser;
    private WebEngine webEngine;

    Map<Integer, String> tariffs = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = browser.getEngine();
        webEngine.setJavaScriptEnabled(true);
        String map = "file://" + getClass().getResource("ymap.html").getPath();
        webEngine.load(map);

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.select(
                        new String[]{ConstTariffsDB.ID, ConstTariffsDB.COST_FOR_MINUTE, ConstTariffsDB.MAX_SPEED},
                        ConstTariffsDB.TABLE_NAME);

                try (ResultSet resultSet = statement.executeQuery(query)) {
                    String tariff;
                    while (resultSet.next()) {
                        tariff = resultSet.getInt(ConstTariffsDB.COST_FOR_MINUTE) +
                                "р. в минуту | до " + resultSet.getInt(ConstTariffsDB.MAX_SPEED) + "км/ч";
                        tariffs.put(resultSet.getInt(ConstTariffsDB.ID), tariff);
                    }
                    ObservableList<String> observableListTariffs = FXCollections.observableArrayList();
                    observableListTariffs.addAll(tariffs.values());
                    comboBoxTariffs.setItems(observableListTariffs);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
            }
        }

        paneTripWithInfo.setVisible(false);
        paneParking.setDisable(true);
        paneSelected.setDisable(true);
        paneTrip.setVisible(false);
        buttonForAdmin.setVisible(user.getIsAdmin());
        buttonForAdmin.setDisable(!user.getIsAdmin());
        buttonMenu.setDisable(false);
        labelBalance.setText(user.getBalance() + "р");
    }

    @FXML
    void onClickStartTrip(MouseEvent event) {
        if (comboBoxTariffs.getValue() != null) {
            user.startTripTimeMS = Date.from(Instant.now()).getTime();
            user.setTripIsActive(true);

            paneTrip.setVisible(true);
            buttonFinishTrip.setText("Для завершения выберите парковку");

            AnchorPane.setBottomAnchor(paneParking, -paneParking.getPrefHeight());
            AnchorPane.setRightAnchor(paneSelected, -paneSelected.getPrefWidth());
            paneParking.setDisable(true);
            paneSelected.setDisable(true);

            buttonMenu.setDisable(true);
            if (user.getIsAdmin()) {
                buttonForAdmin.setDisable(true);
            }

            //In the labelSelectedInfo there is IDscooter in 3 position
            selectedScooterID = Integer.parseInt(labelSelectedInfo.getText().split(" ")[2]);

            for (int tariffID : tariffs.keySet()) {
                if (tariffs.get(tariffID).equals(comboBoxTariffs.getValue())) {
                    DatabaseHandler handler = new DatabaseHandler();
                    if (handler.open()) {
                        try (Statement statement = handler.createStatement()) {
                            String query = SQLHelper.select(new String[]{ConstTariffsDB.COST_FOR_MINUTE, ConstTariffsDB.MAX_SPEED},
                                    ConstTariffsDB.TABLE_NAME,
                                    ConstTariffsDB.ID + "='" + tariffID + "'");
                            try (ResultSet resultSet = statement.executeQuery(query)) {
                                selectedTariff = new Tariff(tariffID,
                                        resultSet.getInt(ConstTariffsDB.COST_FOR_MINUTE),
                                        resultSet.getInt(ConstTariffsDB.MAX_SPEED));
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } finally {
                            handler.close();
                        }
                    }
                }
            }

            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                try (Statement statement = handler.createStatement()) {
                    Date dateNow = Date.from(Instant.now());
                    SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd hh:mm:ss a zzz");
                    String query = SQLHelper.insert(ConstTripsHistoryDB.TABLE_NAME,
                            new String[]{
                                    ConstTripsHistoryDB.ID_USER,
                                    ConstTripsHistoryDB.ID_SCOOTER,
                                    ConstTripsHistoryDB.ID_TARIFF,
                                    ConstTripsHistoryDB.TIME_START,
                                    ConstTripsHistoryDB.ID_PARKING_START,
                                    ConstTripsHistoryDB.IS_FINISHED},
                            new String[]{
                                    String.valueOf(user.getID()),
                                    String.valueOf(selectedScooterID),
                                    String.valueOf(selectedTariff.getID()),
                                    formatForDateNow.format(dateNow),
                                    String.valueOf(selectedParking),
                                    "0"});
                    statement.executeUpdate(query);


                    query = SQLHelper.update(ConstScootersDB.TABLE_NAME, new String[]{ConstScootersDB.ID_PARKING_SPACE + "=0"}, ConstScootersDB.ID + "=" + selectedScooterID);
                    statement.executeUpdate(query);
                    query = SQLHelper.select(new String[]{ConstScootersDB.BATTERY_CHARGE},
                            ConstScootersDB.TABLE_NAME,
                            ConstScootersDB.ID + "='" + selectedScooterID + "'");
                    try (ResultSet resultSet = statement.executeQuery(query)) {
                        selectedScooterCharge = resultSet.getInt(ConstScootersDB.BATTERY_CHARGE);
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } finally {
                    handler.close();
                }
            }
        }
    }

    @FXML
    void onClickFinishTrip(MouseEvent event) {
        buttonFinishTrip.setText("Поездка завершена!");
        buttonFinishTrip.setDisable(true);

        buttonMenu.setDisable(false);
        if (user.getIsAdmin()) {
            buttonForAdmin.setDisable(false);
        }

        int minutes = (int) ((Date.from(Instant.now()).getTime() - user.startTripTimeMS) / 1000 / 60 + 1);
        labelTimeTrip.setText("Время поездки ~ " + minutes);
        if (minutes >= 10 && minutes <= 20)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минут");
        else if (minutes % 10 == 1)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минута");
        else if (minutes % 10 >= 2 && minutes % 10 <= 4)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минуты");
        else
            labelTimeTrip.setText(labelTimeTrip.getText() + " минут");

        labelCostTrip.setText("Стоимость поездки: " + selectedTariff.getCostForMinute() * minutes + "р.");

        labelChargeScooterOnTrip.setText("Заряд: " + (selectedScooterCharge -= 5 * minutes) + "%");

        user.changeBalance(-(selectedTariff.getCostForMinute() * minutes));
        labelBalance.setText(user.getBalance() + "р");


        new Thread(() -> {
            paneTripWithInfo.setVisible(true);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (paneTrip.getOpacity() >= 0 || paneTripWithInfo.getOpacity() >= 0) {
                paneTrip.setOpacity(paneTrip.getOpacity() - 0.05);
                paneTripWithInfo.setOpacity(paneTripWithInfo.getOpacity() - 0.06);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            paneTrip.setVisible(false);
            paneTripWithInfo.setVisible(false);
            paneTrip.setOpacity(1);
            paneTripWithInfo.setOpacity(1);
        }).start();


        user.setTripIsActive(false);

        DatabaseHandler handler = new DatabaseHandler();
        if (handler.open()) {
            try (Statement statement = handler.createStatement()) {
                String query = SQLHelper.update(ConstScootersDB.TABLE_NAME,
                        new String[]{ConstScootersDB.ID_PARKING_SPACE + "=" + selectedParking, ConstScootersDB.BATTERY_CHARGE + "=" + selectedScooterCharge},
                        ConstScootersDB.ID + "=" + selectedScooterID);
                statement.executeUpdate(query);

                query = SQLHelper.selectMax(
                        ConstTripsHistoryDB.ID,
                        "tripID",
                        ConstTripsHistoryDB.TABLE_NAME,
                        ConstTripsHistoryDB.ID_USER + "=" + user.getID());
                System.out.println(query);
                int tripID;
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    tripID = resultSet.getInt("tripID");
                }

                Date dateNow = Date.from(Instant.now());
                SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd hh:mm:ss a zzz");
                query = SQLHelper.update(ConstTripsHistoryDB.TABLE_NAME,
                        new String[]{
                                ConstTripsHistoryDB.IS_FINISHED + "=1",
                                ConstTripsHistoryDB.TIME_FINISH + "='" + formatForDateNow.format(dateNow) + "'",
                                ConstTripsHistoryDB.COST + "='" + (selectedTariff.getCostForMinute() * minutes) + "'",
                                ConstTripsHistoryDB.ID_PARKING_FINISH + "='" + selectedParking + "'"},
                        ConstTripsHistoryDB.ID + "=" + tripID + "");
                statement.executeUpdate(query);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                handler.close();
            }
        }
    }

    @FXML
    private AnchorPane paneTripWithInfo;

    @FXML
    void onClickShowInfoAboutTrip(MouseEvent event) {
        int minutes = (int) ((Date.from(Instant.now()).getTime() - user.startTripTimeMS) / 1000 / 60 + 1);
        labelTimeTrip.setText("Время поездки ~ " + minutes);
        if (minutes >= 10 && minutes <= 20)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минут");
        else if (minutes % 10 == 1)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минута");
        else if (minutes % 10 >= 2 && minutes % 10 <= 4)
            labelTimeTrip.setText(labelTimeTrip.getText() + " минуты");
        else
            labelTimeTrip.setText(labelTimeTrip.getText() + " минут");

        labelCostTrip.setText("Стоимость поездки: " + selectedTariff.getCostForMinute() * minutes + "р.");

        labelChargeScooterOnTrip.setText("Заряд: " + (selectedScooterCharge - 5 * minutes) + "%");


        new Thread(() -> {
            paneTripWithInfo.setVisible(true);
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (paneTripWithInfo.getOpacity() >= 0) {
                paneTripWithInfo.setOpacity(paneTripWithInfo.getOpacity() - 0.06);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            paneTripWithInfo.setVisible(false);
            paneTripWithInfo.setOpacity(1);
        }).start();

    }

    @FXML
    private VBox vbox;
    @FXML
    private AnchorPane paneParking;
    @FXML
    private AnchorPane paneSelected;
    @FXML
    private AnchorPane paneTrip;
    @FXML
    private Button buttonFinishTrip;

    @FXML
    private Label labelTimeTrip;


    @FXML
    private Label labelCostTrip;

    @FXML
    private Label labelChargeScooterOnTrip;

    @FXML
    void onClickHideParking(MouseEvent event) {
        Animations.hideSlideBottom(paneParking);

        paneParking.setDisable(true);
        vbox.getChildren().clear();
    }

    @FXML
    void onClickHideSelection(MouseEvent event) {
        Animations.hideSlideRight(paneSelected);

        paneParking.setDisable(false);
        paneSelected.setDisable(true);
    }

    @FXML
    private Label labelParkingInfo;
    @FXML
    private Label labelSelectedInfo;

    @FXML
    private Label labelSelectedScooterBatteryCharge;
    @FXML
    private Label labelSelectedScooterModel;
    @FXML
    private Label labelSelectedScooterCondition;
    @FXML
    private ComboBox<String> comboBoxTariffs;

    private int selectedParking = 0;
    private int selectedScooterID = 0;
    private int selectedScooterCharge = 0;
    private Tariff selectedTariff = null;

    @FXML
    void onClickSelectParking(MouseEvent event) {
        org.w3c.dom.Document doc = webEngine.getDocument();
        boolean toFind = false;
        for (int i = 0; i < 7; i++) {
            org.w3c.dom.Element el = null;
            switch (i) {
                case 0:
                    el = doc.getElementById("p0");
                    break;
                case 1:
                    el = doc.getElementById("p1");
                    break;
                case 2:
                    el = doc.getElementById("p2");
                    break;
                case 3:
                    el = doc.getElementById("p3");
                    break;
                case 4:
                    el = doc.getElementById("p4");
                    break;
                case 5:
                    el = doc.getElementById("p5");
                    break;
                case 6:
                    el = doc.getElementById("p6");
                    break;
            }
            if (el.getAttribute("balloon").equals("open")) {
                // System.out.println("ID: " + (i + 1));
                selectedParking = i + 1;
                webEngine.executeScript("document.getElementById(\"p" + i + "\").setAttribute(\"balloon\", \"close\");");
                toFind = true;
                break;
            }
        }
        if (toFind) {
            if (!user.isTripIsActive()) {
                // System.out.println("ID: " + el.getAttribute("id") + "\nBalloon Content: " + el.getAttribute("balloonContent"));
                if (paneParking.isDisable() && paneSelected.isDisable()) {
                    paneParking.setDisable(false);

                    Animations.showSlideBottom(paneParking);
                }
                DatabaseHandler handler = new DatabaseHandler();
                if (handler.open()) {

                    try (Statement statement = handler.createStatement()) {
                        String query = "SELECT " + ConstScootersDB.TABLE_NAME + "." + ConstScootersDB.ID + "," + ConstScootersDB.BATTERY_CHARGE + "," + ConstModelsDB.MODEL_NAME + "," + ConstModelsDB.IMAGE + "," + ConstConditionsDB.CONDITION +
                                " FROM " + ConstScootersDB.TABLE_NAME +
                                " INNER JOIN " + ConstModelsDB.TABLE_NAME + " ON " + ConstScootersDB.ID_MODEL + " = " + ConstModelsDB.TABLE_NAME + "." + ConstModelsDB.ID +
                                " INNER JOIN " + ConstConditionsDB.TABLE_NAME + " ON " + ConstScootersDB.ID_CONDITION + " = " + ConstConditionsDB.TABLE_NAME + "." + ConstConditionsDB.ID +
                                " WHERE " + ConstScootersDB.ID_PARKING_SPACE + " = '" + selectedParking + "'";
                        try (ResultSet resultSet = statement.executeQuery(query)) {
                            vbox.getChildren().clear();

                            List<Scooter> scooters = new ArrayList<>();
                            while (resultSet.next()) {
                                scooters.add(new Scooter(resultSet.getInt(ConstScootersDB.ID),
                                        resultSet.getString(ConstModelsDB.MODEL_NAME),
                                        selectedParking, resultSet.getInt(ConstScootersDB.BATTERY_CHARGE),
                                        resultSet.getString(ConstConditionsDB.CONDITION),
                                        resultSet.getString(ConstModelsDB.IMAGE)));
                            }

                            Comparator<Scooter> comparator = (s1, s2) -> {
                                if (s1.getBatteryCharge() > s2.getBatteryCharge()) return -1;
                                if (s1.getBatteryCharge() < s2.getBatteryCharge()) return 1;
                                return 0;
                            };

                            scooters.sort(comparator);

                            for (Scooter scooter : scooters) {
                                CustomPane cp = new CustomPane(scooter, paneParking, paneSelected, labelSelectedInfo,
                                        labelSelectedScooterBatteryCharge, labelSelectedScooterModel, labelSelectedScooterCondition);
                                if (cp != null)
                                    vbox.getChildren().add(cp.show());
                                else
                                    System.out.println("Ошибка с созданием панели");
                            }
                            scooters.removeAll(scooters);
                            scooters = null;

                            int numberScooters = vbox.getChildren().size();
                            labelParkingInfo.setText("На парковке №" + selectedParking + " - " + numberScooters);
                            if (numberScooters >= 10 && numberScooters <= 20)
                                labelParkingInfo.setText(labelParkingInfo.getText() + " самокатов");
                            else if (numberScooters % 10 == 1)
                                labelParkingInfo.setText(labelParkingInfo.getText() + " самокат");
                            else if (numberScooters % 10 >= 2 && numberScooters % 10 <= 4)
                                labelParkingInfo.setText(labelParkingInfo.getText() + " самоката");
                            else
                                labelParkingInfo.setText(labelParkingInfo.getText() + " самокатов");

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        handler.close();
                    }
                }

            } else {
                if (buttonFinishTrip.isDisable()) {
                    buttonFinishTrip.setDisable(false);
                }
                buttonFinishTrip.setText("Завершить поездку на парковке №" + selectedParking);
            }
        } else {
            if (!buttonFinishTrip.isDisable()) {
                buttonFinishTrip.setDisable(true);
                buttonFinishTrip.setText("Для завершения выберите парковку");
            }
            System.out.println("nothing");
        }


    }

    @FXML
    public void onClickGoAdminMenu(MouseEvent event) {
        Scenes.sceneChange(event, "admin-menu.fxml", new AdminMenuController(user));
    }

    @FXML
    void onClickMenu(MouseEvent event) {
        Scenes.sceneChange(event, "menu.fxml", new MenuController(user));
    }


}