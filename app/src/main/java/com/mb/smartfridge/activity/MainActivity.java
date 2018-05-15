package com.mb.smartfridge.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DeviceAdapter;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DeviceEntity;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.utils.NavigationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ListView lvDrawerlayout;
    private ListView lvDevice;
    private DrawerLayout drawerLayout;
    private RelativeLayout leftMenu;
    private DrawerLayoutAdapter drawerlayoutAdapter;
    private DeviceAdapter mDeviceAdapter;
    private ImageView ivSearch;
    private TextView tvSearch;
    private LinearLayout llyCancelback,llyAdddevice;
    private List<DrawerlayoutEntity> list = new ArrayList<>();
    private int img[] = new int[]{R.mipmap.ic_about,R.mipmap.ic_store,R.mipmap.ic_edit_password,R.mipmap.ic_logout};
    private String[] text = new String[]{"关于我们","线上商城","修改密码","退出登录"};
    private List<DeviceEntity> mDeviceEntityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("选择设备");
        initDrawerLayout();
        initDeviceList();
        initView();
    }
    private void setTitle(String title) {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        ImageView imgLeft = findViewById(R.id.btn_left);
        imgLeft.setVisibility(View.VISIBLE);
        imgLeft.setImageResource(R.mipmap.ic_menu);
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(leftMenu)) {
                    drawerLayout.closeDrawer(leftMenu);
                } else {
                    drawerLayout.openDrawer(leftMenu);
                }
            }
        });
    }

    private void initView() {
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        tvSearch = findViewById(R.id.tv_search);
        final AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
        anim.start();
        final Handler handler = new Handler();
        Runnable runnable ;
        runnable = new Runnable() {
            @Override
            public void run() {
                ivSearch.setVisibility(View.GONE);
                tvSearch.setText("我的设备");
                llyCancelback.setVisibility(View.GONE);
                llyAdddevice.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(runnable,2000);

        drawerLayout = findViewById(R.id.dl_content_main_menu);
        leftMenu = findViewById(R.id.ll_left_menu);
        lvDrawerlayout = findViewById(R.id.lv_drawerlayout);
        lvDevice =findViewById(R.id.lv_device);
        llyCancelback =findViewById(R.id.lly_cancelback);
        llyAdddevice =findViewById(R.id.lly_adddevice);
        drawerlayoutAdapter = new DrawerLayoutAdapter(MainActivity.this,list);
        lvDrawerlayout.setAdapter(drawerlayoutAdapter);
        mDeviceAdapter = new DeviceAdapter(MainActivity.this,mDeviceEntityList);
        lvDevice.setAdapter(mDeviceAdapter);
        lvDrawerlayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();//点击子项的时候关闭侧滑栏
                switch (position){
                    case 0:
                        NavigationHelper.startActivity(MainActivity.this,AboutUsActivity.class,null,false);
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
            }
        });

    }
    private void initDrawerLayout(){
        for (int i = 0;i < img.length;i++) {
            list.add(new DrawerlayoutEntity(img[i],text[i]));
        }
    }
    private void initDeviceList(){
        for (int i = 0;i < 3;i++) {
            mDeviceEntityList.add(new DeviceEntity("智能车载冰箱","G3JJ4KK5I8-6FR5"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.im_setting:
//
//                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(leftMenu)) {
            drawerLayout.closeDrawer(leftMenu);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
