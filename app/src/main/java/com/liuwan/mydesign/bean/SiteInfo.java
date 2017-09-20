package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/11/11.
 * 监测站点信息
 */
public class SiteInfo {
    // 站点名称
    private String name;
    // 纬度
    private double latitude;
    // 经度
    private double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public SiteInfo() {
        super();
    }

    public SiteInfo(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "SiteInfo{" +
                "name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

}
