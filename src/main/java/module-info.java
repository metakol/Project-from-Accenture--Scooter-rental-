module com.program.projectscooters1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires javafx.graphics;
    requires sqlite.jdbc;

    opens com.example.program to javafx.fxml;
    exports com.example.program;
    exports com.example.databases;
    opens com.example.databases to javafx.fxml;
    exports com.example.helpers;
    opens com.example.helpers to javafx.fxml;
    exports com.example.dependencies;
    opens com.example.dependencies to javafx.fxml;
}