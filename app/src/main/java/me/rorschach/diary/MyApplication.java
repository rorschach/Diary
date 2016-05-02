package me.rorschach.diary;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * 自定义类继承自系统的Application类
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();               //必须先调用父类的同名方法
        FlowManager.init(this);         //初始化DbFlow这个第三方数据库
        JodaTimeAndroid.init(this);     //初始化JodaTime这个时间库
    }

    @Override
    public void onTerminate() {
        super.onTerminate();            //必须先调用父类的同名方法
        FlowManager.destroy();          //释放DbFlow这个第三方数据库使用的所有资源
    }
}
