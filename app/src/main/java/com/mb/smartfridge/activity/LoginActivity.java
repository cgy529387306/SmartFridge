package com.mb.smartfridge.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.mb.smartfridge.R;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.ProgressDialogHelper;
import com.mb.smartfridge.utils.ProjectHelper;
import com.mb.smartfridge.utils.ToastHelper;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etTel;
    private EditText etPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login);
        initView();
        initNext();
    }

    private void setTitle(String title) {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
    }

    private void initView() {
        etTel = (EditText) findViewById(R.id.et_tel);
        etPwd = (EditText) findViewById(R.id.et_pwd);

        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.tv_login).setOnClickListener(this);
    }

    private void initNext(){

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                NavigationHelper.startActivity(LoginActivity.this,RegisterActivity.class,null,false);
                break;
            case R.id.tv_forget_pwd:
                NavigationHelper.startActivity(LoginActivity.this,ForgetPwdActivity.class,null,false);
                break;
            case R.id.tv_login:
//                NavigationHelper.startActivity(LoginActivity.this,MainActivity.class,null,true);
                doLogin();
                break;
            default:
                break;
        }
    }

    private void doLogin(){
        String mobile = etTel.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            showToast(getString(R.string.input_correct_tel));
            return;
        }else if (TextUtils.isEmpty(password)){
            showToast(getString(R.string.input_password));
            return;
        }else if (!ProjectHelper.isMobiPhoneNum(mobile)) {
            showToast(getString(R.string.tel_error));
            return;
        }else if (!ProjectHelper.isPwdValid(password)) {
            showToast(getString(R.string.password_error));
            return;
        }
        ProgressDialogHelper.showProgressDialog(this,getString(R.string.logining));
        AVUser.loginByMobilePhoneNumberInBackground(mobile, password, new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                ProgressDialogHelper.dismissProgressDialog();
                if (e == null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    ProjectHelper.showErrorMessage(e.getMessage());
                }
            }
        });
    }

    // region 双击返回
    private static final long DOUBLE_CLICK_INTERVAL = 2000;
    private long mLastClickTimeMills = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastClickTimeMills > DOUBLE_CLICK_INTERVAL) {
            ToastHelper.showToast(getString(R.string.more_back_exit));
            mLastClickTimeMills = System.currentTimeMillis();
            return;
        }
        finish();
    }
    // endregion 双击返回


    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

}
