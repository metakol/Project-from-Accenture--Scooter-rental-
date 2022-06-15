package com.example.dependencies;

public class TripHistory {
    private int ID;
    private int IDUser;
    private int IDScooter;
    private int IDTariff;
    private boolean isFinished;
    private int cost;
    private int IDParkingStart;
    private int IDParkingFinish;
    private String timeStart;
    private String timeFinish;


    public TripHistory(int ID, int IDUser, int IDScooter, int IDTariff, boolean isFinished, int cost, int IDParkingStart, int IDParkingFinish, String timeStart, String timeFinish) {
        this.ID = ID;
        this.IDUser = IDUser;
        this.IDScooter = IDScooter;
        this.IDTariff = IDTariff;
        this.isFinished = isFinished;
        this.cost = cost;
        this.IDParkingStart = IDParkingStart;
        this.IDParkingFinish = IDParkingFinish;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
    }

    public int getID() {
        return ID;
    }

    public int getIDUser() {
        return IDUser;
    }

    public int getIDScooter() {
        return IDScooter;
    }

    public int getIDTariff() {
        return IDTariff;
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public int getCost() {
        return cost;
    }

    public int getIDParkingStart() {
        return IDParkingStart;
    }

    public int getIDParkingFinish() {
        return IDParkingFinish;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeFinish() {
        return timeFinish;
    }
}
