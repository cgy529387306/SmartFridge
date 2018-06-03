package com.mb.smartfridge.activity;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.avos.avoscloud.AVUser;
import com.mb.smartfridge.R;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.Utils;


/**
 * 起始页
 *
 * @author @author chenqm on 2018/1/15.
 */

public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏x
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Utils.StatusBarIconManager.MIUI(this, Utils.StatusBarIconManager.TYPE.BLACK);
            Utils.StatusBarIconManager.Flyme(this, Utils.StatusBarIconManager.TYPE.BLACK);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AVUser.getCurrentUser() != null) {
                    NavigationHelper.startActivity(SplashActivity.this,MainActivity.class,null,true);
                }else{
                    NavigationHelper.startActivity(SplashActivity.this,MainActivity.class,null,true);
                }
            }
        }, 1500);
    }


}
