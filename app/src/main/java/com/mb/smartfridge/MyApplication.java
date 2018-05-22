package com.mb.smartfridge;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;


public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;// 初始化
        AVOSCloud.initialize(this,"ytXG1WjAwPsNSUvj27XoPpnE-gzGzoHsz", "MymJLa9fEM9LOqcsUNH7H8vc");
        AVOSCloud.setDebugLogEnabled(true);
        AVAnalytics.enableCrashReport(this, true);
    }

    public static Context getAppContext() {
        return  mContext;
    }
}
