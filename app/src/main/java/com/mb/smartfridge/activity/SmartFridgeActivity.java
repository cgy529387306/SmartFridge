package com.mb.smartfridge.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.mb.smartfridge.R;
import com.mb.smartfridge.observer.Observer;
import com.mb.smartfridge.observer.ObserverManager;
import com.mb.smartfridge.utils.OrderHelper;
import com.mb.smartfridge.utils.ProjectHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class SmartFridgeActivity extends BaseActivity implements View.OnClickListener ,Observer {

    private BleDevice bleDevice;
    private BluetoothGattCharacteristic characteristic;
    private TextView tvCurrentTemp,tvSetTemp;
    private ImageView ivBatteryState,ivEnergyState;
    private TextView tvBatteryState,tvEnergyState;
    private TextView tvBatteryVoltage,tvBatteryQuantity;
    private BluetoothAdapter bluetoothAdapter;
    private final Timer timer = new Timer();
    private TimerTask task;
    private static final String getDataOrder = "AAC0F000000000000000000000005B";
    private static final String powerOffOrder = "AAC0F101000000000000000000005D";
    private String currentTemp,setTemp;
    private String tempUnit;
    private String batteryState,energyState;
    private String highV,lowV;
    private String onOff;
    private String curveTemp;
    private String errorCode;
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
                    sendMessages(getDataOrder);
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
        bleDevice = getIntent().getParcelableExtra("device");
        if (bleDevice == null)
            finish();
        ObserverManager.getInstance().addObserver(this);
        setTitle("车载冰箱");
        initView();
        initListener();
        initTask();
        initBlueManager();
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


    public void initBlueManager() {
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        if (gatt != null){
            for (BluetoothGattService gattService :gatt.getServices()) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    int charaProp = gattCharacteristic.getProperties();
                    if (((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)) {
                        characteristic = gattCharacteristic;
                        openNotify();
                        return;
                    }
                }
            }
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        BleManager.getInstance().clearCharacterCallback(bleDevice);
        ObserverManager.getInstance().deleteObserver(this);
    }


    private void sendMessages(final String hex) {
        if (characteristic==null)
            return;
        BleManager.getInstance().write(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                HexUtil.hexStringToBytes(hex),
                new BleWriteCallback() {

                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(exception.toString());
                            }
                        });
                    }
                });
    }

    private void openNotify(){
        if (characteristic==null)
            return;
        BleManager.getInstance().notify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast( exception.toString());
                            }
                        });
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateView(HexUtil.formatHexString(characteristic.getValue(), true));
                            }
                        });
                    }
                });
    }


    /**
     *
     *【通讯头】【客户码】【指令码】【开关机值】【实时温度值】【曲线温度值】【设置温度值】【模式值】
     0xAA     0xC0    0xF2      0/1       -40~50      -40~50    20~-22       0/1
     【温度单位值】【电池档位值】【错误值】【电池电压值高位】【电池电压值低位】【默认值】
     0/1          0/1/2      1~6         12               26          0x00
     【效验码】
     * @param data
     */
    @SuppressLint("DefaultLocale")
    private void updateView(String data){
        Log.d("data",data);
        if (!TextUtils.isEmpty(data)){
            String[] dataList = data.split(" ");
            if (dataList.length==15){
                onOff = dataList[3];
                currentTemp = dataList[4];
                curveTemp = dataList[5];
                setTemp = dataList[6];
                energyState = dataList[7];
                tempUnit = dataList[8];
                batteryState = dataList[9];
                errorCode = dataList[10];
                highV = dataList[11];
                lowV = dataList[12];
                tvCurrentTemp.setText(String.format("%1s%2s",Integer.parseInt(currentTemp,16), Integer.parseInt(tempUnit,16)==1?"℉":"℃"));
                tvSetTemp.setText(String.format("设置温度：%1s%2s", Integer.parseInt(setTemp,16),Integer.parseInt(tempUnit,16)==1?"℉":"℃"));
                setEnergyState(Integer.parseInt(energyState,16));
                setBatteryState(Integer.parseInt(batteryState,16));
                tvBatteryVoltage.setText(String.format("电池电压：%d.%d", Integer.parseInt(highV, 16), Integer.parseInt(lowV, 16)));
            }
        }
    }

    private String getMessage(){
        String msg = "AAC0F101"+onOff+currentTemp+curveTemp+setTemp+energyState+tempUnit+batteryState+errorCode+highV+lowV;
        String validCode = OrderHelper.makeChecksum(msg);
        return msg+validCode;
    }

    /**
     * @param state 强劲为0x00，节能为0x01
     */
    private void setEnergyState(int state){
        ivEnergyState.setImageResource(state==1?R.mipmap.ic_energy_saving:R.mipmap.ic_energy_strength);
        tvEnergyState.setText(state==1?"节能":"强劲");
    }

    /**
     * @param state 0为低档，1为中档，2为高档
     */
    private void setBatteryState(int state){
        if (state==1){
            ivBatteryState.setImageResource(R.mipmap.ic_battery_middle);
            tvBatteryState.setText("中");
        }else if (state==2){
            ivBatteryState.setImageResource(R.mipmap.ic_battery_hi);
            tvBatteryState.setText("高");
        }else{
            ivBatteryState.setImageResource(R.mipmap.ic_battery_low);
            tvBatteryState.setText("低");
        }
    }


    @Override
    public void onClick(View view) {
        ProjectHelper.disableViewDoubleClick(view);
        int id = view.getId();
        if (id == R.id.iv_minus){
            if (TextUtils.isEmpty(setTemp)){
                setTemp = String.valueOf(Integer.parseInt(setTemp,16)-1);
            }
            sendMessages(getMessage());
        }else if (id == R.id.iv_plus){
            if (TextUtils.isEmpty(setTemp)){
                setTemp = String.valueOf(Integer.parseInt(setTemp,16)+1);
            }
            sendMessages(getMessage());
        }else if (id == R.id.iv_power_off){
            sendMessages(powerOffOrder);
        }
    }


    @Override
    public void disConnected(BleDevice bleDevice) {
        showToast("连接已断开");
        finish();
    }


}
