package com.mb.smartfridge.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DeviceAdapter;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DeviceEntity;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.utils.CommonUtils;
import com.mb.smartfridge.utils.DialogHelper;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.views.DividerItemDecoration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSION_ACCESS_LOCATION = 1;

    private ListView lvDrawerlayout;
    private RecyclerView lvDevice;
    private DrawerLayout drawerLayout;
    private RelativeLayout leftMenu;
    private DrawerLayoutAdapter drawerlayoutAdapter;
    private DeviceAdapter deviceAdapter;

    private TextView tvName;

    private LinearLayout llyDevice; //蓝牙设备列表
    private LinearLayout llyOpenBluetooth; // 开启蓝牙界面
    private ImageView ivSearch;
    private TextView tvSearch;
    private LinearLayout llyCancelBack;  // 底部  取消返回 重新搜索
    private TextView tvAddDevice; //底部  添加设备
    private LinearLayout llyNoDevice; //  设备界面
    private List<DrawerlayoutEntity> list = new ArrayList<>();
    private int img[] = new int[]{R.mipmap.ic_about,R.mipmap.ic_store,R.mipmap.ic_edit_password,R.mipmap.ic_logout};
    private String[] text = new String[]{"关于我们","线上商城","修改密码","退出登录"};
    private List<DeviceEntity> mDeviceEntityList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> deviceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("选择设备");
        initDrawerLayout();
        initView();
        initListener();
        initBlueManager();
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
        tvName = findViewById(R.id.tv_name);
        tvName.setText(AVUser.getCurrentUser().getUsername());

        ivSearch = findViewById(R.id.iv_search);
        tvSearch = findViewById(R.id.tv_search);
        drawerLayout = findViewById(R.id.dl_content_main_menu);
        leftMenu = findViewById(R.id.ll_left_menu);
        lvDrawerlayout = findViewById(R.id.lv_drawerlayout);
        llyDevice = findViewById(R.id.lly_device);
        lvDevice = findViewById(R.id.lv_device);
        llyCancelBack =findViewById(R.id.lly_cancelBack);
        tvAddDevice =findViewById(R.id.tv_addDevice);
        llyOpenBluetooth = findViewById(R.id.lly_openBluetooth);
        llyNoDevice = findViewById(R.id.lly_noDevice);
        drawerlayoutAdapter = new DrawerLayoutAdapter(MainActivity.this,list);
        lvDrawerlayout.setAdapter(drawerlayoutAdapter);

        lvDevice.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        lvDevice.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        deviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(R.layout.item_device,deviceList);
        lvDevice.setAdapter(deviceAdapter);
    }

    private void initListener(){
        tvAddDevice.setOnClickListener(this);
        findViewById(R.id.tv_openBluetooth).setOnClickListener(this);
        findViewById(R.id.tv_reSearch).setOnClickListener(this);
        findViewById(R.id.tv_cancelBack).setOnClickListener(this);
        deviceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int i) {
                if (bluetoothAdapter.isDiscovering())
                    bluetoothAdapter.cancelDiscovery();
                if (deviceList.get(i).getBondState() == BluetoothDevice.BOND_NONE) {
                    bondDevice(i);
                } else if (deviceList.get(i).getBondState() == BluetoothDevice.BOND_BONDED) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",deviceList.get(i));
                    NavigationHelper.startActivity(MainActivity.this,SmartFridgeActivity.class,bundle,false);
                }
            }
        });

        lvDrawerlayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();//点击子项的时候关闭侧滑栏
                switch (position){
                    case 0:
                        NavigationHelper.startActivity(MainActivity.this,AboutUsActivity.class,null,false);
                        break;
                    case 1:
                        NavigationHelper.startActivity(MainActivity.this,SmartFridgeActivity.class,null,false);
                        break;
                    case 2:
                        Bundle bundle = new Bundle();
                        bundle.putInt("type",1);
                        NavigationHelper.startActivity(MainActivity.this,ForgetPwdActivity.class,bundle,false);
                        break;
                    case 3:
                        loginOut();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 初始化蓝牙管理，设置监听
     */
    public void initBlueManager() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(new BluetoothReceiver(), intentFilter);
    }


    private void checkBluetooth() {
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            llyDevice.setVisibility(View.VISIBLE);
            llyOpenBluetooth.setVisibility(View.GONE);
            checkDevice();
        }else {
            llyOpenBluetooth.setVisibility(View.VISIBLE);
            llyDevice.setVisibility(View.GONE);
            bluetoothAdapter.enable();
        }
    }

    private void searchDevice(){
        ivSearch.setVisibility(View.VISIBLE);
        tvSearch.setText("搜索可用设备");
        llyCancelBack.setVisibility(View.VISIBLE);
        tvAddDevice.setVisibility(View.GONE);
        AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
        anim.start();
        requestPermission();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkAccessFinePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkAccessFinePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_ACCESS_LOCATION);
                Log.e(getPackageName(), "没有权限，请求权限");
                return;
            }
            Log.e(getPackageName(), "已有定位权限");
            search();
        }
    }

    public void search() {
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
        Log.e(getPackageName(), "开始搜索");
    }

    private void cancelSearch(){
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        ivSearch.setVisibility(View.GONE);
        tvSearch.setText("我的设备");
        llyCancelBack.setVisibility(View.GONE);
        tvAddDevice.setVisibility(View.VISIBLE);
    }

    private void searchComplete(){
        showToast("搜索完成");
        ivSearch.setVisibility(View.GONE);
        tvSearch.setText("我的设备");
        llyNoDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.VISIBLE:View.GONE);
        lvDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.GONE:View.VISIBLE);
    }

    private void checkDevice() {
        if (mDeviceEntityList.size() > 0){
            ivSearch.setVisibility(View.GONE);
            tvSearch.setText("我的设备");
            llyCancelBack.setVisibility(View.VISIBLE);
        }else { //没有设备就自动搜索
            searchDevice();
        }
    }

    private void initDrawerLayout(){
        for (int i = 0;i < img.length;i++) {
            list.add(new DrawerlayoutEntity(img[i],text[i]));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_openBluetooth:  // 打开蓝牙
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                break;
            case R.id.tv_addDevice:  //  添加设备
                searchDevice();
                break;
            case R.id.tv_reSearch:  //  重新搜索
                searchDevice();
                break;
            case R.id.tv_cancelBack:  //  取消返回
                cancelSearch();
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
    protected void onResume() {
        super.onResume();
        checkBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(getPackageName(), "开启权限permission granted!");
                    search();
                } else {
                    showToast("没有定位权限，请先开启!");
                    Log.e(getPackageName(), "没有定位权限，请先开启!");
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (drawerLayout.isDrawerOpen(leftMenu)) {
            drawerLayout.closeDrawer(leftMenu);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // region 双击返回
    private static final long DOUBLE_CLICK_INTERVAL = 2000;
    private long mLastClickTimeMills = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastClickTimeMills > DOUBLE_CLICK_INTERVAL) {
            ToastHelper.showToast("再按一次返回退出");
            mLastClickTimeMills = System.currentTimeMillis();
            return;
        }
        finish();
    }
    // endregion 双击返回


    /**
     * 退出登录
     */
    private void loginOut(){
        //注销账号
        DialogHelper.showConfirmDialog(MainActivity.this, "注销", "确定要退出当前账号？", true,
                R.string.dialog_positive, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AVUser.getCurrentUser().logOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }

                }, R.string.dialog_negative, null);
    }

    private void bondDevice(int i) {
        try {
            Method method = BluetoothDevice.class.getMethod("createBond");
            Log.e(getPackageName(), "开始配对");
            method.invoke(deviceList.get(i));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null){
                return;
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
                Log.e(getPackageName(), "找到新设备了");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                boolean addFlag = true;
                for (BluetoothDevice bluetoothDevice : deviceList) {
                    if (device.getAddress().equals(bluetoothDevice.getAddress())) {
                        addFlag = false;
                    }
                }

                if (addFlag) {
                    deviceList.add(device);
                    deviceAdapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                searchComplete();
            }else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        Log.e(getPackageName(), "取消配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.e(getPackageName(), "配对中");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.e(getPackageName(), "配对成功");
                        break;
                }


            }
        }
    }



}
