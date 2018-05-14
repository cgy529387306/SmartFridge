package com.mb.smartfridge.activity;

import android.app.Activity;
import android.icu.util.VersionInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.utils.ActivityManager;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public abstract class BaseActivity extends AppCompatActivity {
    public static final String Tag = BaseActivity.class.getSimpleName();
    public final static int MSG_UPDATE_APP = 12003;
    public static String EXTRA_FLAG = "com.bearya.flag";
    private RelativeLayout mLoadingView;
    private TextView mTitle;
    private ImageView mTitleLeft;
    private ImageView mTitleRight;
    protected boolean isResume = false;
    private long lastBackTime = 0;
    private boolean isSupportExit = false;
    private List<VersionInfo> mVersionInfos = new ArrayList<>();
    private boolean isInitRobotUpdater = false;

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


    public Activity getActivityParent() {
        if (this.getParent() != null) {
            return this.getParent();
        }
        return this;
    }

    public synchronized void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public synchronized void closeLoadingView() {
        mLoadingView.setVisibility(View.GONE);
    }


    public void exitApp() {
        ActivityManager.getInstance().closeAllActivity();
        finish();
    }

    public void showToast(String text){
        ToastHelper.showToast(text);
    }
}
