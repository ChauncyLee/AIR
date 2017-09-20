package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/11/10.
 * 污染物参数信息
 */
public class ParameterInfo {
    // 污染物名称
    private String name;
    // 正常值下限
    private double lower;
    // 正常值上限
    private double upper;
    // 降解系数
    private double degradation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    public double getDegradation() {
        return degradation;
    }

    public void setDegradation(double degradation) {
        this.degradation = degradation;
    }

    public ParameterInfo() {
        super();
    }

    public ParameterInfo(String name, double lower, double upper, double degradation) {
        this.name = name;
        this.lower = lower;
        this.upper = upper;
        this.degradation = degradation;
    }

    @Override
    public String toString() {
        return "ParameterInfo{" +
                "name='" + name + '\'' +
                ", lower=" + lower +
                ", upper=" + upper +
                ", degradation=" + degradation +
                '}';
    }

}
