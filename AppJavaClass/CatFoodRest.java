package com.example.mola;

public class CatFoodRest {
    private String dat;
    private int restT;


    public CatFoodRest() {
    }

    public String getDat() {
        return dat;
    }

    public void setDat(String dat) {
        this.dat = dat;
    }
    public String getRestT() {
        String time = String.valueOf(restT)+ "/5";
        return time;
    }

    public void setRestT(int restT) {
        this.restT = restT;
    }
}
