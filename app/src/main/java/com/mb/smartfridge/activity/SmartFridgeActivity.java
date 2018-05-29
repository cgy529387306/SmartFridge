package com.mb.smartfridge.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.mb.smartfridge.R;
import com.mb.smartfridge.observer.Observer;
import com.mb.smartfridge.observer.ObserverManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cgy on 2018/4/19 0019.
 */

public class SmartFridgeActivity extends BaseActivity implements View.OnClickListener ,Observer {

    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private TextView tvCurrentTemp,tvSetTemp;
    private ImageView ivBatteryState,ivEnergyState;
    private TextView tvBatteryState,tvEnergyState;
    private TextView tvBatteryVoltage,tvBatteryQuantity;
    private BluetoothAdapter bluetoothAdapter;
    private final Timer timer = new Timer();
    private TimerTask task;
    private boolean isRequestSuccess;
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
                    if (isRequestSuccess){
                        getMessage();
                    }
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
                    if (((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) && ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0)
                            && ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)) {
                        characteristic = gattCharacteristic;
                        sendMessage("AAC0F000000000000000000000005B");
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


    private void sendMessage(final String hex) {
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
                        isRequestSuccess = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast( "write success, current: " + current
                                        + " total: " + total
                                        + " justWrite: " + HexUtil.formatHexString(justWrite, true));
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

    private void getMessage() {
        if (characteristic==null)
            return;
        BleManager.getInstance().read(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleReadCallback() {

                    @Override
                    public void onReadSuccess(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast( HexUtil.formatHexString(data, true));
                            }
                        });
                    }

                    @Override
                    public void onReadFailure(final BleException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast( exception.toString());
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
                                showToast(HexUtil.formatHexString(characteristic.getValue(), true));
                            }
                        });
                    }
                });
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


    @Override
    public void disConnected(BleDevice bleDevice) {
        showToast("连接已断开");
        finish();
    }
}
