package com.example.dependencies;

public class Tariff {
    private int ID;
    private int costForMinute;
    private int maxSpeed;

    public Tariff(int ID, int costForMinute, int maxSpeed) {
        this.ID = ID;
        this.costForMinute = costForMinute;
        this.maxSpeed = maxSpeed;
    }

    public int getID() {
        return ID;
    }

    public int getCostForMinute() {
        return costForMinute;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }


}
