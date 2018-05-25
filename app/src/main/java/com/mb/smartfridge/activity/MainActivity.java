package com.mb.smartfridge.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.calypso.bluelib.bean.MessageBean;
import com.calypso.bluelib.bean.SearchResult;
import com.calypso.bluelib.listener.OnConnectListener;
import com.calypso.bluelib.listener.OnReceiveMessageListener;
import com.calypso.bluelib.listener.OnSearchDeviceListener;
import com.calypso.bluelib.listener.OnSendMessageListener;
import com.calypso.bluelib.manage.BlueManager;
import com.calypso.bluelib.utils.TypeConversion;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mb.smartfridge.R;
import com.mb.smartfridge.adapter.DeviceAdapter;
import com.mb.smartfridge.adapter.DrawerLayoutAdapter;
import com.mb.smartfridge.entity.DeviceEntity;
import com.mb.smartfridge.entity.DrawerlayoutEntity;
import com.mb.smartfridge.utils.CommonUtils;
import com.mb.smartfridge.utils.DialogHelper;
import com.mb.smartfridge.utils.NavigationHelper;
import com.mb.smartfridge.utils.ProgressDialogHelper;
import com.mb.smartfridge.utils.ToastHelper;
import com.mb.smartfridge.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

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
    private List<SearchResult> deviceList;
    private BlueManager blueManager;

    private OnConnectListener onConnectListener;
    private OnSendMessageListener onSendMessageListener;
    private OnSearchDeviceListener onSearchDeviceListener;
    private OnReceiveMessageListener onReceiveMessageListener;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = msg.obj.toString();
            showToast(message);
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
        }
    };
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
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String mac = deviceList.get(position).getAddress();
                blueManager.connectDevice(mac);
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
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    Toast.makeText(MainActivity.this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }
            }
        }
        onSearchDeviceListener = new OnSearchDeviceListener() {
            @Override
            public void onStartDiscovery() {
                sendMessage(0, "正在搜索设备..");
                Log.d(TAG, "onStartDiscovery()");
            }

            @Override
            public void onNewDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "new device: " + device.getName() + " " + device.getAddress());
            }

            @Override
            public void onSearchCompleted(List<SearchResult> bondedList, List<SearchResult> newList) {
                Log.d(TAG, "SearchCompleted: bondedList" + bondedList.toString());
                Log.d(TAG, "SearchCompleted: newList" + newList.toString());
                ivSearch.setVisibility(View.GONE);
                tvSearch.setText("我的设备");
                deviceList.clear();
                deviceList.addAll(newList);
                deviceAdapter.notifyDataSetChanged();
                sendMessage(0, CommonUtils.isEmpty(deviceList)?"无可连接设备":"搜索完成,点击列表进行连接！");
                llyNoDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.VISIBLE:View.GONE);
                lvDevice.setVisibility(CommonUtils.isEmpty(deviceList)?View.GONE:View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                sendMessage(0, "搜索失败");
            }
        };
        onConnectListener = new OnConnectListener() {
            @Override
            public void onConnectStart() {
                ProgressDialogHelper.showProgressDialog(MainActivity.this,"连接中...");
                Log.i("blue", "onConnectStart");
            }

            @Override
            public void onConnectting() {
                Log.i("blue", "onConnectting");
            }

            @Override
            public void onConnectFailed() {
                ProgressDialogHelper.dismissProgressDialog();
                sendMessage(0, "连接失败！");
                Log.i("blue", "onConnectFailed");
            }

            @Override
            public void onConectSuccess(String mac) {
                ProgressDialogHelper.dismissProgressDialog();
                sendMessage(4, "连接成功 MAC: " + mac);
                Log.i("blue", "onConectSuccess");
            }

            @Override
            public void onError(Exception e) {
                ProgressDialogHelper.dismissProgressDialog();
                sendMessage(0, "连接异常！");
                Log.i("blue", "onError");
            }
        };
        onSendMessageListener = new OnSendMessageListener() {
            @Override
            public void onSuccess(int status, String response) {
                sendMessage(0, "发送成功！");
                Log.i("blue", "send message is success ! ");
            }

            @Override
            public void onConnectionLost(Exception e) {
                sendMessage(0, "连接断开！");
                Log.i("blue", "send message is onConnectionLost ! ");
            }

            @Override
            public void onError(Exception e) {
                sendMessage(0, "发送失败！");
                Log.i("blue", "send message is onError ! ");
            }
        };
        onReceiveMessageListener = new OnReceiveMessageListener() {


            @Override
            public void onProgressUpdate(String what, int progress) {
                sendMessage(1, what);
            }

            @Override
            public void onDetectDataUpdate(String what) {
                sendMessage(3, what);
            }

            @Override
            public void onDetectDataFinish() {
                sendMessage(2, "接收完成！");
                Log.i("blue", "receive message is onDetectDataFinish");
            }

            @Override
            public void onNewLine(String s) {
                sendMessage(3, s);
            }

            @Override
            public void onConnectionLost(Exception e) {
                sendMessage(0, "连接断开");
                Log.i("blue", "receive message is onConnectionLost ! ");
            }

            @Override
            public void onError(Exception e) {
                Log.i("blue", "receive message is onError ! ");
            }
        };
        blueManager = BlueManager.getInstance(getApplicationContext());
        blueManager.setOnSearchDeviceListener(onSearchDeviceListener);
        blueManager.setOnConnectListener(onConnectListener);
        blueManager.setOnSendMessageListener(onSendMessageListener);
        blueManager.setOnReceiveMessageListener(onReceiveMessageListener);
        blueManager.requestEnableBt();
    }

    /**
     * @param type    0 修改状态  1 更新进度  2 体检完成  3 体检数据进度
     * @param context
     */
    public void sendMessage(int type, String context) {
        if (handler != null) {
            Message message = handler.obtainMessage();
            message.what = type;
            message.obj = context;
            handler.sendMessage(message);
        }
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
        }
    }

    private void searchDevice(){
        ivSearch.setVisibility(View.VISIBLE);
        tvSearch.setText("搜索可用设备");
        llyCancelBack.setVisibility(View.VISIBLE);
        tvAddDevice.setVisibility(View.GONE);
        AnimationDrawable anim = (AnimationDrawable) ivSearch.getBackground();
        anim.start();
        blueManager.setReadVersion(false);
        blueManager.searchDevices();
    }

    private void cancelSearch(){
        ivSearch.setVisibility(View.GONE);
        tvSearch.setText("我的设备");
        llyCancelBack.setVisibility(View.GONE);
        tvAddDevice.setVisibility(View.VISIBLE);
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
    protected void onResume() {
        super.onResume();
        checkBluetooth();//检测蓝牙是否开启
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (blueManager != null) {
            blueManager.close();
            blueManager = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
            if (permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.
                        permission.ACCESS_COARSE_LOCATION)) {
                    return;
                }
            }
        }
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

    private void getSn(){
        MessageBean item = new MessageBean(TypeConversion.getDeviceVersion());
        blueManager.setReadVersion(true);
        blueManager.sendMessage(item, true);
    }

    private void send(){
        blueManager.setReadVersion(false);
        MessageBean item = new MessageBean(TypeConversion.startDetect());
        blueManager.sendMessage(item, true);
    }

}
