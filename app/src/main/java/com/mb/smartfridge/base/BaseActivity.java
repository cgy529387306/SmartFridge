package com.mb.smartfridge.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.mb.smartfridge.R;
import com.mb.smartfridge.utils.ActivityManager;
import com.mb.smartfridge.utils.EmptyHelper;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.utils.Utils;


/**
 * Created by chenqm on 17/7/18.
 */
@SuppressWarnings("unchecked")
public class BaseActivity extends AppCompatActivity{

    private RelativeLayout rlActionbar;
    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivAction;
    private RelativeLayout rlRoot;
    private TextView tvAction;
    protected Context context;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.common_actionbar_back);
        context = this;
        RxBus.get().register(this);
        ActivityManager.getInstance().putActivity(getClass().getName(), this);
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Utils.StatusBarIconManager.MIUI(this, Utils.StatusBarIconManager.TYPE.BLACK);
            Utils.StatusBarIconManager.Flyme(this, Utils.StatusBarIconManager.TYPE.BLACK);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        ActivityManager.getInstance().removeActivity(getClass().getName());
    }

    private void initView() {
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        rlActionbar = (RelativeLayout) findViewById(R.id.rl_actionbar);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivAction = (ImageView) findViewById(R.id.iv_action);
        tvAction = (TextView) findViewById(R.id.tv_action);
        ivBack.setOnClickListener(listener);
        ivAction.setOnClickListener(listener);
        tvAction.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.iv_back) {
                onBack();
            } else if(id == R.id.iv_action) {
                onIvAction();
            } else if(id == R.id.tv_action) {
                onTvAction();
            }
        }
    };

    protected void onBack() {
        finish();
    }

    protected void onIvAction() {

    }

    protected void onTvAction() {

    }


    public void applyBackground() {
        getWindow().getDecorView().getRootView().setBackgroundColor(getResources().getColor(R.color.white));
    }


    public void setContentView(int resId) {
        applyBackground();
        View view = getLayoutInflater().inflate(resId, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, R.id.rl_actionbar);
        if (null != rlRoot)
            rlRoot.addView(view, lp);
    }

    public void setContentView(int resId,String title) {
        setContentView(resId);
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
    }


    public void setContentView(int resId,String title,String action) {
        setContentView(resId);
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(action)){
            tvAction.setText(action);
            tvAction.setVisibility(View.VISIBLE);
        }
    }

    public void setContentView(int resId,String title,int ivRes) {
        setContentView(resId);
        if (!TextUtils.isEmpty(title)){
            tvTitle.setText(title);
        }
        if (EmptyHelper.isNotEmpty(ivRes)){
            ivAction.setImageResource(ivRes);
            ivAction.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 设置中间标题文字
     *
     * @param c
     */
    public void setTitleText(CharSequence c) {
        if (tvTitle != null)
            tvTitle.setText(c);
    }

    /**
     * 设置中间标题文字
     *
     * @param resId
     */
    public void setTitleText(int resId) {
        if (tvTitle != null)
            tvTitle.setText(resId);
    }





    /**
     * @ author:gy 2014年8月7日 下午2:53:01
     */
    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * 隐藏返回按钮
     */
    public void hideBack(){
        ivBack.setVisibility(View.INVISIBLE);
    }

    /**
     * 显示返回按钮
     */
    public void showBack(){
        ivBack.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏
     */
    public void hideActionbar(){
        rlActionbar.setVisibility(View.GONE);
    }

    /**
     * 隐藏标题栏
     */
    public void showActionbar(){
        rlActionbar.setVisibility(View.VISIBLE);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public void showToast(String text){
        ToastHelper.showToast(text);
    }


}

