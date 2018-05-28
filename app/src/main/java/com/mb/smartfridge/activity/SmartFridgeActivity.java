package com.mb.smartfridge.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.smartfridge.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class SmartFridgeActivity extends BaseActivity implements View.OnClickListener{

    private BluetoothDevice bluetoothDevice;
    private UUID uuid = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");//服务端和客户端统一
    private TextView tvCurrentTemp,tvSetTemp;
    private ImageView ivBatteryState,ivEnergyState;
    private TextView tvBatteryState,tvEnergyState;
    private TextView tvBatteryVoltage,tvBatteryQuantity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceiver bluetoothReceiver;
    private final Timer timer = new Timer();
    private TimerTask task;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(SmartFridgeActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    getMessage();
                    break;
                case 3:
                    String data = (String) msg.obj;
                    Toast.makeText(SmartFridgeActivity.this, "获取成功 data："+data, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartfridge);
        bluetoothDevice = getIntent().getParcelableExtra("device");
        setTitle("车载冰箱");
        initView();
        initListener();
        initBlueManager();
        initTask();
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

    private void initView(){
        tvCurrentTemp = findViewById(R.id.tv_current_temperature);
        tvSetTemp = findViewById(R.id.tv_set_temperature);
        ivBatteryState = findViewById(R.id.iv_battery_state);
        ivEnergyState = findViewById(R.id.iv_energy_state);
        tvBatteryState = findViewById(R.id.tv_battery_state);
        tvEnergyState = findViewById(R.id.tv_energy_state);
        tvBatteryVoltage = findViewById(R.id.tv_battery_voltage);
        tvBatteryQuantity = findViewById(R.id.tv_battery_quantity);
    }

    private void initListener(){
        findViewById(R.id.iv_minus).setOnClickListener(this);
        findViewById(R.id.iv_plus).setOnClickListener(this);
        findViewById(R.id.iv_power_off).setOnClickListener(this);
    }

    private void initTask(){
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 1000, 2000);
    }

    /**
     * 初始化蓝牙管理，设置监听
     */
    public void initBlueManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothReceiver = new BluetoothReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        this.unregisterReceiver(bluetoothReceiver);
    }

    private void sendMessage(final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                try {
                    BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    os = socket.getOutputStream();
                    os.write(msg.getBytes());
                    os.flush();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    BluetoothServerSocket serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("serverSocket", uuid);
                    handler.sendEmptyMessage(3);
                    BluetoothSocket accept = serverSocket.accept();
                    is = accept.getInputStream();

                    byte[] bytes = new byte[1024];
                    int length = is.read(bytes);
                    Message msg = new Message();
                    msg.what = 3;
                    msg.obj = new String(bytes, 0, length);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_minus){
            //TODO
        }else if (id == R.id.iv_plus){
            //TODO
        }else if (id == R.id.iv_power_off){
           finish();
        }
    }

    class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null){
                return;
            }
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
                        bondDevice();
                    default:
                        break;
                }
            }else if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i("TAG", "BluetoothAdapter is turning on.");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i("TAG", "BluetoothAdapter is on.");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i("TAG", "BluetoothAdapter is turning off.");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i("TAG", "BluetoothAdapter is off.");
                        showToast("蓝牙已经关闭，请重新打开");
                        Intent openIntent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(openIntent);
                        break;
                }
            }
        }
    }

    private void bondDevice() {
        try {
            Method method = BluetoothDevice.class.getMethod("createBond");
            Log.e(getPackageName(), "开始配对");
            method.invoke(bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
