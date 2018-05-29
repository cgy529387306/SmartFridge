package com.mb.smartfridge.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.mb.smartfridge.utils.ActivityManager;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.utils.Utils;

import java.util.Calendar;


public abstract class BaseActivity extends AppCompatActivity {
    public static final String Tag = BaseActivity.class.getSimpleName();
    private long lastBackTime = 0;
    private boolean isSupportExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().putActivity(getClass().getName(), this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Utils.StatusBarIconManager.MIUI(this, Utils.StatusBarIconManager.TYPE.BLACK);
            Utils.StatusBarIconManager.Flyme(this, Utils.StatusBarIconManager.TYPE.BLACK);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isSupportExit && keyCode == KeyEvent.KEYCODE_BACK) {
            long nowTime = Calendar.getInstance().getTimeInMillis();
            if (nowTime - lastBackTime < 1000) {
                exitApp();
            } else {//按下的如果是BACK，同时没有重复
                showToast("再按一次退出应用！");
            }
            lastBackTime = nowTime;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(getClass().getName());
    }

    protected void onBack() {
        finish();
    }

    public void exitApp() {
        ActivityManager.getInstance().closeAllActivity();
        finish();
    }

    public void showToast(String text){
        Log.d("toast",text);
        ToastHelper.showToast(text);
    }
}
