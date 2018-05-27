package com.mb.smartfridge.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mb.smartfridge.R;

import java.io.OutputStream;
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
                    getData();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
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

    private void getData(){

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
}
