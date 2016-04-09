package com.example.potoyang.bikelock;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * @FileName: com.example.potoyang.bikelock.MyApplication.java
 * @author: Yuchuan Yang
 * @data:2016-03-31 20:09
 * @Description: 用来初始化app的全局变量
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组间之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
    }
}