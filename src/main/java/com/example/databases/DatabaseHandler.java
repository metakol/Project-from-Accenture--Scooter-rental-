package com.example.databases;

import com.example.dependencies.User;
import javafx.scene.control.Label;

import java.sql.*;

public class DatabaseHandler {
    private static Connection connection;
    static private final String URL = "jdbc:sqlite:src/main/resources/com/example/forDatabase/ProjectScootersDB.db";

    public boolean open() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
