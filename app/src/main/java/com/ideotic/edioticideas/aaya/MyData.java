package com.ideotic.edioticideas.aaya;

/**
 * Created by Shubham on 17-05-2016.
 */
public class MyData {
    String city;
    String temp, text;
    String humid;
    String[][] week;

    public void setCity(String city) {
        this.city = city;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setHumid(String humid) {
        this.humid = humid;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setWeek(String[][] week) {
        this.week = week;
    }

    public String getTemp() {
        return week[0][3] + "\n" + temp + "\n Highest" + week[0][0] + "\n Lowest" + week[0][1] + "\n" + text + "\n" + humid;
    }

    public String getWeek() {
        String resullt = null;
        for (int i = 0; i < 7; i++) {
            resullt = resullt + week[i][3] + "\n Highest" + week[i][0] + "\n Lowest" + week[i][1] + "\n" + week[i][2] + "\n";
        }
        return resullt;
    }
}
