package com.moutamid.randomchat.Models;

public class VipCost {

    private String month1;
    private String month3;
    private String year;

    public VipCost(){

    }

    public VipCost(String month1, String month3, String year) {
        this.month1 = month1;
        this.month3 = month3;
        this.year = year;
    }

    public String getMonth1() {
        return month1;
    }

    public void setMonth1(String month1) {
        this.month1 = month1;
    }

    public String getMonth3() {
        return month3;
    }

    public void setMonth3(String month3) {
        this.month3 = month3;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
