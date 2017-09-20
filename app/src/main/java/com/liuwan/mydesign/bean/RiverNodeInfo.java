package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/12/5.
 * 河流结点信息
 */
public class RiverNodeInfo {
    // 结点id
    private int id;
    // 纬度
    private double latitude;
    // 经度
    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public RiverNodeInfo() {
        super();
    }

    public RiverNodeInfo(int id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "RiverNodeInfo{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
