package com.mb.smartfridge.activity;

import android.annotation.SuppressLint;
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
    private TextView tvTempC,tvTempF;
    private final Timer timer = new Timer();
    private TimerTask task;
    private static final String getDataOrder = "AAC0F000000000000000000000005B";//获取数据指令
    private static final String powerOffOrder = "AAC0F100000000000000000000005C";//关机指令
    private static final String powerOnOrder = "AAC0F101000000000000000000005D";//开机指令
    private static final String energyStrongOrder = "AAC0F101000000000000000000025F";//强劲模式指令
    private static final String energySaveOrder = "AAC0F1010000000100000000000260";//技能模式指令
    private static final String batteryHighOrder = "AAC0F1010000000000020000000463";//高档指令
    private static final String batteryMiddleOrder = "AAC0F1010000000000010000000462";//中档指令
    private static final String batteryLowerOrder = "AAC0F1010000000000000000000461";//抵挡指令
    private static final String tempCOrder = "AAC0F1010000000000000000000360";//摄氏温度
    private static final String tempFOrder = "AAC0F1010000000001000000000361";//华氏温度
    private String currentTemp,setTemp;
    private String tempUnit;
    private String batteryState,energyState;
    private String highV,lowV;
    private boolean isOpen = true;
    private ImageView ivPower;
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
        setTitle("TAWA智能车载冰箱");
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
        ivPower = findViewById(R.id.iv_power);
        tvTempC = findViewById(R.id.tv_tempC);
        tvTempF = findViewById(R.id.tv_tempF);
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
        tvTempC.setOnClickListener(this);
        tvTempF.setOnClickListener(this);
        ivPower.setOnClickListener(this);
        findViewById(R.id.iv_minus).setOnClickListener(this);
        findViewById(R.id.iv_plus).setOnClickListener(this);
        findViewById(R.id.lin_energy).setOnClickListener(this);
        findViewById(R.id.lin_battery).setOnClickListener(this);
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
                                Log.d("myTag",TextUtils.isEmpty(exception.toString())?"":exception.toString());
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
                                Log.d("myTag",TextUtils.isEmpty(exception.toString())?"":exception.toString());
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
                isOpen = getHexResult(dataList[3])==1;
                currentTemp = dataList[4];
                setTemp = dataList[6];
                energyState = dataList[7];
                tempUnit = dataList[8];
                batteryState = dataList[9];
                highV = dataList[11];
                lowV = dataList[12];
                ivPower.setImageResource(isOpen?R.mipmap.ic_power_on :R.mipmap.ic_power_off);
                tvCurrentTemp.setText(String.format("%1s%2s",getHexResult(currentTemp), getHexResult(tempUnit)==1?"℉":"℃"));
                tvSetTemp.setText(String.format("设置温度：%1s%2s", getHexResult(setTemp),getHexResult(tempUnit)==1?"℉":"℃"));
                tvTempC.setTextColor(getHexResult(tempUnit)==1?getResources().getColor(R.color.colorGray):getResources().getColor(R.color.colorBounder));
                tvTempC.setTextColor(getHexResult(tempUnit)==1?getResources().getColor(R.color.colorBounder):getResources().getColor(R.color.colorGray));
                setEnergyState(getHexResult(energyState));
                setBatteryState(getHexResult(batteryState));

                tvBatteryVoltage.setText(String.format("电池电压：%d.%d",Integer.parseInt(highV,16), Integer.parseInt(lowV,16)));
            }
        }
    }

    private void setTemperature(String tempHex){
        String hex = "00";
        if (TextUtils.isEmpty(tempHex)){
            hex = "00";
        }else if (tempHex.length()==1){
            hex = "0"+tempHex;
        }else if (tempHex.length()==2){
            hex = tempHex;
        }
        String msg = "AAC0F1010000"+hex+"00000000000001";
        String validCode = OrderHelper.makeChecksum(msg);
        String order = msg+validCode;
        sendMessages(order.toUpperCase());
    }

    private int getHexResult(String data){
        int result = 0;
        try {
            int iData = Integer.parseInt(data,16);
            result = iData>128?iData-256:iData;
        }catch (Exception e){
            e.printStackTrace();
        }
       return result;
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
        }else if (state == 0){
            ivBatteryState.setImageResource(R.mipmap.ic_battery_low);
            tvBatteryState.setText("低");
        }
    }


    @Override
    public void onClick(View view) {
        ProjectHelper.disableViewDoubleClick(view);
        int id = view.getId();
        if (id == R.id.iv_minus){
            if (!TextUtils.isEmpty(setTemp)){
                int curTemp = getHexResult(setTemp);
                curTemp = curTemp-1;
                if (curTemp<0){
                    curTemp = curTemp+256;
                }
                setTemp = Integer.toHexString(curTemp);
            }
            setTemperature(setTemp);
        }else if (id == R.id.iv_plus){
            if (!TextUtils.isEmpty(setTemp)){
                int curTemp = getHexResult(setTemp);
                curTemp = curTemp+1;
                if (curTemp<0){
                    curTemp = curTemp+256;
                }
                setTemp = Integer.toHexString(curTemp);
            }
            setTemperature(setTemp);
        }else if (id == R.id.iv_power){
            sendMessages(isOpen?powerOffOrder:powerOnOrder);
        }else if (id == R.id.lin_energy){
            int state = getHexResult(energyState);
            sendMessages(state==1?energyStrongOrder:energySaveOrder);
        }else if (id == R.id.lin_battery){
            int state = getHexResult(batteryState);
            if (state == 0){
                sendMessages(batteryMiddleOrder);
            }else if (state == 1){
                sendMessages(batteryHighOrder);
            }else if (state == 2){
                sendMessages(batteryLowerOrder);
            }
        }else if (id == R.id.tv_tempC){
            if (getHexResult(tempUnit)!=1){
                sendMessages(tempCOrder);
            }
        }else if (id == R.id.tv_tempF){
            if (getHexResult(tempUnit)==1){
                sendMessages(tempFOrder);
            }
        }
    }


    @Override
    public void disConnected(BleDevice bleDevice) {
        showToast("连接已断开");
        finish();
    }


}
