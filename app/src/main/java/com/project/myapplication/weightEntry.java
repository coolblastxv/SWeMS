package com.project.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class weightEntry {

    private Double weight;
    private Date timestamp;
    private Double BMI;

    private weightEntry(Double weight, Date timestamp, Double BMI){
        this.weight = weight;
        this.timestamp = timestamp;
        this.BMI = BMI;

    }
    public weightEntry() {

    }
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getTimestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {

        this.timestamp = timestamp;
    }


    public Double getBMI() {
        return BMI;
    }

    public void setBMI(Double BMI) {
       BMI = BMI;
    }
}
