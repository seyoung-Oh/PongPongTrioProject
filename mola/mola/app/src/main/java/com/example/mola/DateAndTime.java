package com.example.mola;

public class DateAndTime {
    private int restT;
    private String DAT;

    public DateAndTime() {
    }

    public DateAndTime(int k, String d){
        this.restT = k;
        this.DAT = d;
    }


    public String getDAT() {
        return DAT;
    }

    public void setDAT(String DAT) {
        this.DAT = DAT;
    }

    public int getRestT() {
        return restT;
    }

    public void setRestT(int restT) {
        this.restT = restT;
    }
}
