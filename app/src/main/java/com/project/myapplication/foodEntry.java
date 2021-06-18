package com.project.myapplication;

import java.util.Date;

public class foodEntry {

    private String food;
    private Integer calorie;

    private foodEntry(String food, Integer calorie) {
        this.food = food;
        this.calorie = calorie;
    }
    public foodEntry() {

    }
    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public Integer getCalorie() {
        return calorie;
    }

    public void setCalorie(Integer calorie) {
        this.calorie = calorie;
    }
}
