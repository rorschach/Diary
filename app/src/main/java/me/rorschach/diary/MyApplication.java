package me.rorschach.diary;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by lei on 16-1-18.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }
}
