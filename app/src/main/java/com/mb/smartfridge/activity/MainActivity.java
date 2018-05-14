package com.mb.smartfridge.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.utils.NavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ListView lvDrawerlayout;
    private ImageView setting;
    private DrawerLayout drawerLayout;
    private RelativeLayout leftMenu;
    private DrawerLayoutAdapter drawerlayoutAdapter;
    private List<DrawerlayoutEntity> list = new ArrayList<>();
    private int img[] = new int[]{R.mipmap.ic_about,R.mipmap.ic_store,R.mipmap.ic_edit_password,R.mipmap.ic_logout};
    private String[] text = new String[]{"关于我们","线上商城","修改密码","退出登录"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawerLayout();
        initView();
    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_content_main_menu);
        leftMenu = (RelativeLayout) findViewById(R.id.ll_left_menu);
        setting = (ImageView) findViewById(R.id.im_setting);
        lvDrawerlayout = findViewById(R.id.lv_drawerlayout);
        setting.setOnClickListener(this);
        findViewById(R.id.im_control).setOnClickListener(this);
        drawerlayoutAdapter = new DrawerLayoutAdapter(MainActivity.this,list);
        lvDrawerlayout.setAdapter(drawerlayoutAdapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_setting:
                if (drawerLayout.isDrawerOpen(leftMenu)) {
                    drawerLayout.closeDrawer(leftMenu);
                } else {
                    drawerLayout.openDrawer(leftMenu);
                }
                break;
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
