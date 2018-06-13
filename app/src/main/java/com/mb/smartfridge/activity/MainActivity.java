package com.mb.smartfridge.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.mb.smartfridge.MyApplication;
import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DeviceAdapter;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.entity.VersionInfo;
import com.mb.smartfridge.utils.CommonUtils;
import com.mb.smartfridge.utils.DialogHelper;
import com.mb.smartfridge.utils.JsonHelper;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.ProgressDialogHelper;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private BluetoothAdapter bluetoothAdapter;
    private List<BleDevice> deviceList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("选择设备");
        initDrawerLayout();
        initView();
        initListener();
        initBlueManager();
        checkVersion();
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
                BleDevice bleDevice = deviceList.get(i);
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device",deviceList.get(i));
                    NavigationHelper.startActivity(MainActivity.this,SmartFridgeActivity.class,bundle,false);
                }else {
                    BleManager.getInstance().cancelScan();
                    connectDevice(bleDevice);
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
                        NavigationHelper.openTM(MainActivity.this);
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

    private void checkVersion(){
        AVQuery<AVObject> avQuery = new AVQuery<>("app_version");
        avQuery.getInBackground("5b1f9dfca22b9d003a48a79e", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                Log.d("result",avObject.toString());
                VersionInfo versionInfo = JsonHelper.fromJson(avObject.toString(), VersionInfo.class);
                if (versionInfo!=null && versionInfo.getServerData()!=null){
                    String code = CommonUtils.getVersionName(MyApplication.getAppContext());
                    VersionInfo.ServerDataBean dataBean = versionInfo.getServerData();
                    if (getVersion(code)<getVersion(dataBean.getVersion())){
                        DialogHelper.showConfirmDialog(MainActivity.this, CommonUtils.isNotEmpty(dataBean.getTitle())?dataBean.getTitle():"版本更新", dataBean.getTips(), getVersion(dataBean.getForce_reboot())==0,
                                R.string.dialog_positive, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openUrlWithBrowser("https://fir.im/akcg");
                                    }

                                }, R.string.dialog_negative, null);
                    }
                }
            }
        });
    }

    private int getVersion(String version){
        try {
            if (CommonUtils.isNotEmpty(version) && version.contains(".")){
                version = version.replace(".","");
            }
            return Integer.parseInt(version);
        }catch (Exception e){
            return 0;
        }
    }

    private void openUrlWithBrowser(String url){
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化蓝牙管理，设置监听
     */
    public void initBlueManager() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(true, "WaymanBleQX")   // 只扫描指定广播名的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }


    private void checkBluetooth() {
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()){
            llyDevice.setVisibility(View.VISIBLE);
            llyOpenBluetooth.setVisibility(View.GONE);
            searchDevice();
        }else {
            llyOpenBluetooth.setVisibility(View.VISIBLE);
            llyDevice.setVisibility(View.GONE);
        }
    }

    private void searchDevice(){
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
            startScan();
        }
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                ivSearch.setVisibility(View.VISIBLE);
                tvSearch.setText("搜索可用设备");
                llyCancelBack.setVisibility(View.VISIBLE);
                tvAddDevice.setVisibility(View.GONE);
                AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
                anim.start();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                boolean addFlag = true;
                for (int i=0;i<deviceList.size();i++) {
                    BleDevice bluetoothDevice = deviceList.get(i);
                    if (bleDevice.getKey().equals(bluetoothDevice.getKey())) {
                        addFlag = false;
                        deviceList.set(i,bleDevice);
                    }
                }
                if (addFlag) {
                    deviceList.add(bleDevice);
                    connectDevice(bleDevice);
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                searchComplete();
            }
        });
    }


    private void cancelSearch(){
        BleManager.getInstance().cancelScan();
        ivSearch.setVisibility(View.GONE);
        tvSearch.setText("我的设备");
        llyCancelBack.setVisibility(View.GONE);
        tvAddDevice.setVisibility(View.VISIBLE);
    }

    private void searchComplete(){
        Log.d("myTag","搜索完成");
        ivSearch.setVisibility(View.GONE);
        tvSearch.setText("我的设备");
        llyNoDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.VISIBLE:View.GONE);
        lvDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.GONE:View.VISIBLE);
    }

    private void connectDevice(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.d("myTag","连接失败");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                setBleDevice(bleDevice);
                deviceAdapter.notifyDataSetChanged();
                Log.d("myTag","连接成功");
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                deviceList.remove(bleDevice);
                deviceAdapter.notifyDataSetChanged();
                ProgressDialogHelper.dismissProgressDialog();
                Log.d("myTag","取消连接成功");
            }
        });
    }

    private void setBleDevice(BleDevice bleDevice){
        for (int i=0; i<deviceList.size();i++){
            BleDevice device = deviceList.get(i);
            if (bleDevice.getKey().equals(device.getKey())){
                deviceList.set(i,bleDevice);
            }
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
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(getPackageName(), "开启权限permission granted!");
                    startScan();
                } else {
                    showToast("没有定位权限，请先开启!");
                    Log.e(getPackageName(), "没有定位权限，请先开启!");
                }
            }
            break;
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



}
