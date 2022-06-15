package com.example.dependencies;

import com.example.databases.ConstUsersDB;
import com.example.databases.DatabaseHandler;
import com.example.helpers.SQLHelper;

import java.sql.SQLException;
import java.sql.Statement;

public class User {

    private int ID;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private int balance;
    private boolean isAdmin = false;

    private boolean tripIsActive=false;

    public boolean isTripIsActive(){
        return tripIsActive;
    }
    public void setTripIsActive(boolean b){
        tripIsActive=b;
    }

    public long startTripTimeMS =0;


    public User(int ID, String userName, String email, String password, String phoneNumber, int balance, boolean isAdmin) {
        this.ID = ID;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public User(int ID, String userName, String email, String phoneNumber, int balance,boolean isAdmin) {
        this.ID = ID;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.isAdmin = isAdmin;
    }

    public boolean changeBalance(String backCard, String CVVcode, int sum) {
        if (backCard.length() == 16 && CVVcode.length() == 3 && sum > 0) {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                changeBalanceDB(sum, ID, handler);
                this.balance += sum;
                handler.close();
            }
            return true;
        } else {
            System.out.println("карта введена неверно");
        }

        return false;
    }

    public boolean changeBalance(int sum) {
        if (balance + sum >= 0) {
            DatabaseHandler handler = new DatabaseHandler();
            if (handler.open()) {
                changeBalanceDB(sum, ID, handler);
                this.balance += sum;
                handler.close();
                return true;
            }
        } else {
            System.out.println("Недостаточно средств");
        }
        return false;
    }

    private void changeBalanceDB(int sum, int userID, DatabaseHandler handler) {
        try (Statement statement = handler.createStatement()) {
            String query= SQLHelper.update(ConstUsersDB.TABLE_NAME,
                    new String[]{ConstUsersDB.BALANCE + "=" + ConstUsersDB.BALANCE + "+" + sum},
                    ConstUsersDB.ID + "='" + userID + "'");
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

}
