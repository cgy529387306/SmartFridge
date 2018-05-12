package com.mb.smartfridge.activity;

import android.os.Bundle;

import com.mb.smartfridge.R;
import com.mb.smartfridge.api.ApiMethods;
import com.mb.smartfridge.base.BaseActivity;
import com.mb.smartfridge.entity.Subject;
import com.mb.smartfridge.http.subscribers.ProgressSubscriber;
import com.mb.smartfridge.http.subscribers.SubscriberOnNextListener;

import java.util.List;

public class MainActivity extends BaseActivity {
    private SubscriberOnNextListener getTopMovieOnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login,"首页");
        getTopMovieOnNext = new SubscriberOnNextListener<List<Subject>>() {
            @Override
            public void onNext(List<Subject> subjects) {

            }
        };
        getMovie();
    }

    //进行网络请求
    private void getMovie(){
        ApiMethods.getInstance().getTopMovie(new ProgressSubscriber(getTopMovieOnNext, context), 0, 10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
