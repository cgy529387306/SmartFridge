package com.mb.smartfridge.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.utils.ProgressDialogHelper;

import java.io.File;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener{

    private final static String JDShopId = "74489";     //--京东小米官方旗舰店
    private final static String JD_URL_WEB = "https://shop.m.jd.com/?shopId=74489";
    private final static String JD_URL_NATIVE ="openApp.jdMobile://virtual?params={\"category\":\"jump\",\"des\":\"jshopMain\",\"shopId\":\""+JDShopId+"\",\"sourceType\":\"M_sourceFrom\",\"sourceValue\":\"dp\"}";


    //http://zmnxbc.com/s/fkGUY?tm=8c14ba
    //--3.京东和淘宝的包名
    private String mJDMall = "com.jingdong.app.mall";
    private String mTaoBao = "com.taobao.taobao";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        setTitle("关于我们");
        initView();
    }

    private void initView(){
        findViewById(R.id.btn_tm).setOnClickListener(this);
        findViewById(R.id.btn_jd).setOnClickListener(this);
    }

    private void setTitle(String title) {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        ImageView imgLeft = findViewById(R.id.btn_left);
        imgLeft.setVisibility(View.VISIBLE);
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_tm){

        }else if (id == R.id.btn_jd){
            if (isInstallByread(mJDMall)) {
                ProgressDialogHelper.showProgressDialog(AboutUsActivity.this,"加载中...");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(JD_URL_NATIVE));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(JD_URL_WEB));
                startActivity(intent);
            }
        }
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    @Override
    protected void onPause() {
        super.onPause();
        ProgressDialogHelper.dismissProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressDialogHelper.dismissProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ProgressDialogHelper.dismissProgressDialog();
    }
}
