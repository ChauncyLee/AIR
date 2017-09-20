package com.liuwan.mydesign.util;

import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

/**
 * Created by liuwan on 2016/11/18.
 * 测距工具类
 */
public class LatLngUtil {

    /**
     * 地球半径
     */
    private static final double EARTH_RADIUS = 6378137;

    /**
     * 弧度换算
     */
    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
     */
    public static double latLng2Dist(LatLng latLng1, LatLng latLng2) {
        double radLat1 = rad(latLng1.latitude);
        double radLat2 = rad(latLng2.latitude);
        double a = radLat1 - radLat2;
        double b = rad(latLng1.longitude) - rad(latLng2.longitude);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

}
