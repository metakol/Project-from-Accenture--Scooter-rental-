package com.example.dependencies;

public class ParkingSpace {
    private int ID;
    private String geoposition;
    private float radius;

    public ParkingSpace(int ID, String geoposition, float radius) {
        this.ID = ID;
        this.geoposition = geoposition;
        this.radius = radius;
    }

    public int getID() {
        return ID;
    }
    public String getGeoposition() {
        return geoposition;
    }
    public float getRadius() {
        return radius;
    }
}
