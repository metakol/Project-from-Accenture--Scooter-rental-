package com.example.helpers;

import com.example.program.Launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventObject;

public class Scenes {
    public static void sceneChange(EventObject event, String FXMLfile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource(FXMLfile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.out.println("Неверное имя файла");
            e.printStackTrace();
        }
    }

    public static void sceneChange(EventObject event, String FXMLfile, Object controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource(FXMLfile));
            fxmlLoader.setControllerFactory(c -> controller);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.out.println("Неверное имя файла");
            e.printStackTrace();
        }
    }
}
