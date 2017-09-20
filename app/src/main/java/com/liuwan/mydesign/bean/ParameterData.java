package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/11/9.
 * 监测到的污染物数据
 */
public class ParameterData {
    // 污染物名称
    //private String name;
    // 监测到的污染物的值
    private float vale;
    // 监测时间
    private String time;

    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

    public float getVale() {
        return vale;
    }

    public void setVale(float vale) {
        this.vale = vale;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ParameterData() {
        super();
    }

    public ParameterData(String name, float vale, String time) {
       // this.name = name;
        this.vale = vale;
        this.time = time;
    }

   /* @Override
    public String toString() {
        return "ParameterData{" +
                "name='" + name + '\'' +
                ", vale=" + vale +
                ", time='" + time + '\'' +
                '}';
    }*/

    @Override
    public String toString() {
        return "ParameterData{" +
                "vale=" + vale +
                ", time='" + time + '\'' +
                '}';
    }
}
