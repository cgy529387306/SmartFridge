package com.mb.smartfridge.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.calypso.bluelib.bean.SearchResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mb.smartfridge.R;

import java.util.List;

public class DeviceAdapter extends BaseQuickAdapter<SearchResult, BaseViewHolder> {


    public DeviceAdapter(@LayoutRes int layoutResId, @Nullable List<SearchResult> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchResult item) {
        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_model, item.getAddress());
    }

}
