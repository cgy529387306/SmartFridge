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
    private FrameLayout mFlContent;
    private RelativeLayout mRootView;
    private RelativeLayout mLoadingView;
    private TextView mTitle;
    private ImageView mTitleLeft;
    private ImageView mTitleRight;
    private ImageView mSettingWifi;
    protected boolean isResume = false;
    private long lastBackTime = 0;
    private boolean isSupportExit = false;
    private List<VersionInfo> mVersionInfos = new ArrayList<>();
    private boolean isInitRobotUpdater = false;
    private TextView mRightTip;

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

    protected void onRightMenu() {

    }
    protected void onRightTip() {

    }

    public Activity getActivityParent() {
        if (this.getParent() != null) {
            return this.getParent();
        }
        return this;
    }

    public void setContentView(int layout) {
        contentView(0, "", 0, 0, 0, 0, null, layout, null, false);
    }

    protected void setContentView(CharSequence title, int layout) {
        contentView(0, title == null ? "" : title, 0, 0, 0, 0, null, layout, null,
                false);
    }

    protected void setContentView(int title, int layout, int bgId, boolean withMenu) {
        if (title == 0) {
            super.setContentView(layout);
        } else {
            contentView(title, null, bgId, 0, 0, 0, null, layout, null, withMenu);
        }
    }

    protected void setContentView(CharSequence title, View view) {
        contentView(0, title == null ? "" : title, 0, 0, 0, 0, null, 0, view, false);
    }

    protected void setContentView(int title, View view) {
        contentView(title, null, 0, 0, 0, 0, null, 0, view, false);
    }

    protected void setContentView(int title, int rightResId, int layout) {
        contentView(title, null, 0,  0, rightResId, 0, null, layout, null, false);
    }

    protected void setContentView(int title, int layout) {
        contentView(title, null, 0, 0, 0, 0, null, layout, null, false);
    }
    protected void setContentView(int title, int layout ,String rightString) {
        contentView(title, null, 0, 0, 0, 0, rightString, layout, null, false);
    }
    protected void setContentView(String title, int layout ,String rightString) {
        contentView(0, title, 0, 0, 0, 0, rightString, layout, null, false);
    }

    protected void setContentView(String title, int rightResId, int layout) {
        contentView(0, title,  0,  0, rightResId, 0, null, layout, null, false);
    }

    protected void setContentViewAndImage(int title, int rightImageResId,
                                          int layout) {
        if (title == 0) {
            super.setContentView(layout);
        } else {
            contentView(title, null, 0, 0, 0, rightImageResId, null, layout, null,
                    false);
        }
    }

    protected void setContentView(int title, int leftResId, String rightString,
                                  int layout) {
        contentView(title, null, 0, leftResId, 0, 0, rightString, layout, null, false);
    }

    private void contentView(int titleResId, CharSequence title, int bgId, int leftResId,
                             int rightResId, int rightIvResId, String rightString,
                             int layout, View vv, final boolean withMenu) {

        String titleString = title == null ? null : title.toString();
        super.setContentView(R.layout.base_layout);
        mFlContent = (FrameLayout) findViewById(R.id.frame_content);
        mLoadingView = (RelativeLayout) findViewById(R.id.pb_content_loading);
        mRootView = (RelativeLayout) findViewById(R.id.root);
        RelativeLayout rlTitle = (RelativeLayout) findViewById(R.id.rl_title);
        mTitleRight = (ImageView) findViewById(R.id.btn_right);
        mTitleLeft = (ImageView) findViewById(R.id.btn_left);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mRightTip = (TextView) findViewById(R.id.tv_right_tip);
        String titleRes = titleResId > 0 ? getString(titleResId)
                : titleString;
        if (TextUtils.isEmpty(titleRes)) {
            rlTitle.setVisibility(View.GONE);
        } else {
            mTitle.setText(titleRes);
        }
        String rightTip = rightString;
        if (TextUtils.isEmpty(rightTip)) {
            mRightTip.setVisibility(View.GONE);
        } else {
            mRightTip.setVisibility(View.VISIBLE);
            mRightTip.setText(rightString);
        }
        mTitleRight.setOnClickListener(listener);
        mTitleLeft.setOnClickListener(listener);
        mRightTip.setOnClickListener(listener);

        if (leftResId > 0) {
            mTitleLeft.setImageResource(leftResId);
        } else {
            mTitleLeft.setImageResource(R.mipmap.ic_actionbar_back);
        }

        if (rightResId > 0){
            mTitleRight.setImageResource(rightResId);
            mTitleRight.setVisibility(View.VISIBLE);
        }else {
            mTitleRight.setVisibility(View.GONE);
        }

        if (bgId == 0) {
            mRootView.setBackgroundResource(R.color.colorViewBg);
        } else {
            mRootView.setBackgroundResource(bgId);
        }
        if (layout != 0) {
            LayoutInflater inflater = LayoutInflater.from(this);
            mFlContent.addView(inflater.inflate(layout, null));
        } else if (vv != null) {
            mFlContent.addView(vv);
        }
    }

    public synchronized void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public synchronized void closeLoadingView() {
        mLoadingView.setVisibility(View.GONE);
    }

    OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.btn_right) {
                onRightMenu();
            } else if(id == R.id.btn_left) {
                onBack();
            }
            else if(id == R.id.tv_right_tip) {
                onRightTip();
            }
        }
    };

    public void exitApp() {
        ActivityManager.getInstance().closeAllActivity();
        finish();
    }

    public void showToast(String text){
        ToastHelper.showToast(text);
    }
}
