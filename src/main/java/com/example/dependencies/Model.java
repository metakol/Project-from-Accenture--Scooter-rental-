package com.example.dependencies;

public class Model {
    private int ID;
    private String modelName;
    private String image;

    public Model(int ID, String modelName, String image) {
        this.ID = ID;
        this.modelName = modelName;
        this.image = image;
    }

    public int getID() {
        return ID;
    }
    public String getModelName() {
        return modelName;
    }
    public String getImage() {
        return image;
    }
}
