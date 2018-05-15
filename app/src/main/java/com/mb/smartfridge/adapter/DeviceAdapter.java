package com.mb.smartfridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.entity.DeviceEntity;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private List<DeviceEntity> dataList = new ArrayList<>();
    public DeviceAdapter(Context mContext , List<DeviceEntity> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_device, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvModel = (TextView) convertView.findViewById(R.id.tv_model);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(dataList.get(position).getName());
        holder.tvModel.setText(dataList.get(position).getModel());
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvModel;
    }
}
