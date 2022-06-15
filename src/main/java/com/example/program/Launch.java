package com.example.program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class Launch extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("log-in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trotti");
        stage.setScene(scene);
      //  InputStream inputStream=getClass().getResourceAsStream("/com/example/images/icons8_maple_leaf_48px.png");
       // stage.getIcons().add(new Image(inputStream));
        stage.show();
    }
    public static void main(String[] args) {launch();}
}
