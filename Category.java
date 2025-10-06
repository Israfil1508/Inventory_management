package com.company.inventory3.model;

public class Category {
    private int id;
    private String type;


    public Category() {}

    public Category(String type) {
        this.type = type;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @Override
    public String toString() {
        return type;
    }
}