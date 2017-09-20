package com.liuwan.mydesign.bean;

import android.app.Application;

import org.xutils.x;

/**
 * Created by liuwan on 2016/11/3.
 * Application对象
 */
public class MyApplication extends Application {

    public static String appIp = "http://210.29.65.96:65528";

    public static String chanelId = "";
    public static String username = "";
    public static String messageId = "";

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化XUtils3
        x.Ext.init(this);
        //设置debug模式
        x.Ext.setDebug(true);
    }

}
