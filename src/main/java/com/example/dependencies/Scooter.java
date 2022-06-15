package com.example.dependencies;

public class Scooter {
    private int ID;
    private String model;
    private String imageName;
    private int batteryCharge;
    private String condition;
    private int parkingSpace;


    public Scooter(int ID, String model,int parkingSpace, int batteryCharge, String condition, String imageName) {
        this.ID = ID;
        this.model = model;
        this.parkingSpace=parkingSpace;
        this.imageName = imageName;
        this.batteryCharge = batteryCharge;
        this.condition = condition;
    }

    public Scooter(int ID, String model, int parkingSpace, int batteryCharge, String condition) {
        this.ID = ID;
        this.model = model;
        this.batteryCharge = batteryCharge;
        this.parkingSpace = parkingSpace;
        this.condition = condition;
    }

    public int getID() {
        return ID;
    }
    public String getModel() {
        return model;
    }
    public String getImageName() {
        return imageName;
    }
    public int getBatteryCharge() {
        return batteryCharge;
    }
    public String getCondition() {
        return condition;
    }
    public int getParkingSpace() {
        return parkingSpace;
    }
}
