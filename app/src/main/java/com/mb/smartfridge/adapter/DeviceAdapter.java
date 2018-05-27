package com.mb.smartfridge.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mb.smartfridge.R;

import java.util.List;

public class DeviceAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {


    public DeviceAdapter(@LayoutRes int layoutResId, @Nullable List<BluetoothDevice> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_model, item.getAddress());
        helper.setText(R.id.tv_bound_state,item.getBondState() == BluetoothDevice.BOND_BONDED?"连接":"未连接");
        helper.setTextColor(R.id.tv_bound_state,item.getBondState() == BluetoothDevice.BOND_BONDED?mContext.getResources().getColor(R.color.colorBlue):mContext.getResources().getColor(R.color.colorHint));
        helper.setImageResource(R.id.iv_bound_state,item.getBondState() == BluetoothDevice.BOND_BONDED?R.mipmap.ic_arrow_right:R.mipmap.ic_warn_info);
    }

}
