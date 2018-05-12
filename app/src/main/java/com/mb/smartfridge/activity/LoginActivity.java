package com.mb.smartfridge.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.mb.smartfridge.R;
import com.mb.smartfridge.api.ApiMethods;
import com.mb.smartfridge.base.BaseActivity;
import com.mb.smartfridge.entity.UserData;
import com.mb.smartfridge.http.subscribers.ProgressSubscriber;
import com.mb.smartfridge.http.subscribers.SubscriberOnNextListener;
import com.mb.smartfridge.utils.ProjectHelper;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText etTel;
    private EditText etPwd;
    private SubscriberOnNextListener loginOnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_login,"登录");
        hideActionbar();
        initView();
        initNext();
    }

    private void initView() {
        etTel = (EditText) findViewById(R.id.et_tel);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        findViewById(R.id.tv_register).setOnClickListener(this);
        findViewById(R.id.tv_forget_pwd).setOnClickListener(this);
        findViewById(R.id.tv_login).setOnClickListener(this);
    }

    private void initNext(){
        loginOnNext = new SubscriberOnNextListener<UserData>() {
            @Override
            public void onNext(UserData subjects) {
                if (subjects!=null){
                    showToast(subjects.toString());
                }
            }
        };
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
//                NavigationHelper.startActivity(LoginActivity.this,RegisterActivity.class,null,false);
                break;
            case R.id.tv_forget_pwd:
//                NavigationHelper.startActivity(LoginActivity.this,ForgetPwdActivity.class,null,false);
                break;
            case R.id.tv_login:
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
        ApiMethods.getInstance().doLogin(new ProgressSubscriber(loginOnNext, context), mobile, password);
    }

}
