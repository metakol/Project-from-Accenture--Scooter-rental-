package com.example.dependencies;

public class Condition {
    private int ID;
    private String condition;

    public Condition(int ID, String condition) {
        this.ID = ID;
        this.condition = condition;
    }

    public int getID() {
        return ID;
    }

    public String getCondition() {
        return condition;
    }
}
