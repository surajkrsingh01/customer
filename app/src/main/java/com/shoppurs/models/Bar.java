package com.shoppurs.models;

/**
 * Created by Shweta on 5/17/2016.
 */
public class Bar{// implements Comparable<Bar>{

    private int barColor;
    private String name;
    private int saleAchievedValue;
    private int saleTargetValue;
    private int saleValue,budget;

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSaleAchievedValue() {
        return saleAchievedValue;
    }

    public void setSaleAchievedValue(int saleAchievedValue) {
        this.saleAchievedValue = saleAchievedValue;
    }

    public int getSaleTargetValue() {
        return saleTargetValue;
    }

    public void setSaleTargetValue(int saleTargetValue) {
        this.saleTargetValue = saleTargetValue;
    }

    public int getSaleValue() {
        return saleValue;
    }

    public void setSaleValue(int saleValue) {
        this.saleValue = saleValue;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    /* @Override
    public int compareTo(Bar another) {
        double compareValue=((Bar)another).getValue();
        return Double.compare(this.value,compareValue);
    }*/

}
