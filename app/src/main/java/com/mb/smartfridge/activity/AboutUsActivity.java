package com.mb.smartfridge.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.api.ApiMethods;
import com.mb.smartfridge.http.subscribers.ProgressSubscriber;
import com.mb.smartfridge.http.subscribers.SubscriberOnNextListener;
import com.mb.smartfridge.utils.ProjectHelper;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class AboutUsActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        setTitle("关于我们");
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
}
