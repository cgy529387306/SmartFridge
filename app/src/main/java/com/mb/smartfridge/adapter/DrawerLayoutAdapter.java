package com.mb.smartfridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mb.smartfridge.R;
import com.mb.smartfridge.entity.DrawerlayoutEntity;

import java.util.ArrayList;
import java.util.List;

public class DrawerLayoutAdapter extends BaseAdapter {
    private Context mContext;
    private List<DrawerlayoutEntity> dataList = new ArrayList<>();
    public DrawerLayoutAdapter(Context mContext , List<DrawerlayoutEntity> dataList) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_drawerlayout, null);
            holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img_icon.setBackgroundResource(dataList.get(position).getImg());
        holder.tv_text.setText(dataList.get(position).getText());
        return convertView;
    }

    static class ViewHolder {
        ImageView img_icon;
        TextView tv_text;
    }
}
