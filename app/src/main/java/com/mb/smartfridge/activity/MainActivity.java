package com.mb.smartfridge.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DeviceAdapter;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DeviceEntity;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.ToastHelper;

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
    private LinearLayout llyCancelback;  // 底部  取消返回 重新搜索
    private TextView llyAdddevice; //底部  添加设备
    private LinearLayout llyOpenbluetooth; // 开启蓝牙界面
    private TextView tvOpenbluetooth;
    private LinearLayout llyNodevice; //  设备界面
    private TextView tvReSearch; // 重新搜索
    private TextView tvCancelBack; //取消返回
    private List<DrawerlayoutEntity> list = new ArrayList<>();
    private int img[] = new int[]{R.mipmap.ic_about,R.mipmap.ic_store,R.mipmap.ic_edit_password,R.mipmap.ic_logout};
    private String[] text = new String[]{"关于我们","线上商城","修改密码","退出登录"};
    private List<DeviceEntity> mDeviceEntityList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;

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
        drawerLayout = findViewById(R.id.dl_content_main_menu);
        leftMenu = findViewById(R.id.ll_left_menu);
        lvDrawerlayout = findViewById(R.id.lv_drawerlayout);
        lvDevice =findViewById(R.id.lv_device);
        llyCancelback =findViewById(R.id.lly_cancelback);
        llyAdddevice =findViewById(R.id.lly_adddevice);
        llyOpenbluetooth = findViewById(R.id.lly_openbluetooth);
        tvOpenbluetooth = findViewById(R.id.tv_openbluetooth);
        llyNodevice = findViewById(R.id.lly_nodevice);
        tvReSearch = findViewById(R.id.tv_reSearch);
        tvCancelBack = findViewById(R.id.tv_cancelBack);
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
                    case 2:
                        NavigationHelper.startActivity(MainActivity.this,UpdatePwdActivity.class,null,false);
                        break;
                    case 3:
                        NavigationHelper.startActivity(MainActivity.this,LoginActivity.class,null,false);
                        break;
                    default:
                        break;
                }
            }
        });
        tvOpenbluetooth.setOnClickListener(this);
        llyAdddevice.setOnClickListener(this);
        tvReSearch.setOnClickListener(this);
        tvCancelBack.setOnClickListener(this);

    }

    private void initCheckBluetooth() {

         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            ToastHelper.showToast("蓝牙打开了"+ bluetoothAdapter.isEnabled());
            llyOpenbluetooth.setVisibility(View.GONE);
            checkDevice();
        }else {
            ToastHelper.showToast("蓝牙未打开"+ bluetoothAdapter.isEnabled());
            llyOpenbluetooth.setVisibility(View.VISIBLE);
        }
    }

    private void searchDevice(){
        ivSearch.setVisibility(View.VISIBLE);
        tvSearch.setText("搜索可用设备");
        llyCancelback.setVisibility(View.GONE);
        AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
        anim.start();
        Handler handler = new Handler();
        Runnable runnable ;
        runnable = new Runnable() {
            @Override
            public void run() {
                ivSearch.setVisibility(View.GONE);
                tvSearch.setText("我的设备");
                llyAdddevice.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(runnable,2000);
    }

    private void addDevice(){
        ivSearch.setVisibility(View.VISIBLE);
        tvSearch.setText("搜索可用设备");
        llyCancelback.setVisibility(View.VISIBLE);
        llyAdddevice.setVisibility(View.GONE);
        AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
        anim.start();
        Handler handler = new Handler();
        Runnable runnable ;
        runnable = new Runnable() {
            @Override
            public void run() {
                ivSearch.setVisibility(View.GONE);
                tvSearch.setText("我的设备");
            }
        };
        handler.postDelayed(runnable,2000);
    }

    private void checkDevice() {
        llyNodevice.setVisibility(View.VISIBLE);
        // 此时应该有网络请求 得到设备信息  之后再做判断
        if (mDeviceEntityList.size() > 0){
            ivSearch.setVisibility(View.GONE);
            tvSearch.setText("我的设备");
            llyAdddevice.setVisibility(View.VISIBLE);

        }else { //没有设备就自动搜索
            searchDevice();
        }
    }

    private void initDrawerLayout(){
        for (int i = 0;i < img.length;i++) {
            list.add(new DrawerlayoutEntity(img[i],text[i]));
        }
    }
    private void initDeviceList(){
        for (int i = 0;i < 3;i++) {
//            mDeviceEntityList.add(new DeviceEntity("智能车载冰箱","G3JJ4KK5I8-6FR5"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_openbluetooth:  // 打开蓝牙
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                break;
            case R.id.lly_adddevice:  //  添加设备
                addDevice();
                break;
            case R.id.tv_reSearch:  //  重新搜索
                addDevice();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheckBluetooth();//检测蓝牙是否开启

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
