package com.mb.smartfridge.activity;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.annotation.WorkerThread;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.mb.smartfridge.R;
        import com.mb.smartfridge.utils.NavigationHelper;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener{


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
            NavigationHelper.openTM(AboutUsActivity.this);
        }else if (id == R.id.btn_jd){
            NavigationHelper.openJD(AboutUsActivity.this);
        }
    }


    private class myTask extends AsyncTask<Void, Integer, Void>{

        @WorkerThread
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }


}
