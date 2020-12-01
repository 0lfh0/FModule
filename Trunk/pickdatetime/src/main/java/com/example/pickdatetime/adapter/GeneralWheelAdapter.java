package com.example.pickdatetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import com.example.pickdatetime.R;

/**
 * Created by fhf11991 on 2017/8/29.
 */

public class GeneralWheelAdapter extends BaseWheelAdapter {

    protected ArrayList<Integer> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private int selectTextColor = 0xff444444;
    private int textColor = 0xffdddddd;

    public void setData(@NonNull List<Integer> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataChangedEvent();
    }

    @Override
    public int getItemsCount() {
        return mData.size();
    }

    private LayoutInflater getLayoutInflater(Context context) {
        if(mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(context);
        }
        return mLayoutInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.cbk_wheel_default_inner_text, parent, false);
            holder = new ViewHolder();
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        holder.text.setText(getItem(position));
        return convertView;
    }

    @Override
    public void refreshStatus(View convertView, boolean isSelected) {
        TextView textview = (TextView) convertView.findViewById(R.id.text);
        textview.setTextColor(isSelected ? selectTextColor : textColor);
    }

    public String getItem(int position) {
        return String.valueOf(position);
    }

    public int getValue(int position) {
        return mData.get(position);
    }

    private static class ViewHolder {
        public TextView text;
    }
}