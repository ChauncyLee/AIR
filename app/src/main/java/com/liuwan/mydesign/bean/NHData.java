package com.liuwan.mydesign.bean;

/**
 * Created by Cerian on 2017/8/9.
 */

public class NHData {
    private String time;
    private float DataValue;
    public float getDataValue() {
        return DataValue;
    }

    public void setDataValue(float dataValue) {
        DataValue = dataValue;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NHData{" +
                "time='" + time + '\'' +
                ", DataValue=" + DataValue +
                '}';
    }
}
