package com.example.fmodule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmodule.other.ContactData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class ContactsSearchListAdapter extends BaseAdapter {
    private final String TAG = "ContactsSearchListAdapter";
    private Context context;
    private LayoutInflater inflater;
    private boolean multiple;
    private ArrayList<ContactData> listData;
    private LinkedList<ContactData> selectedList;
    private LinkedList<ContactData> noneSelectedList;

    public ContactsSearchListAdapter(Context context, ArrayList<ContactData> listData) {
        if (listData == null) {
            throw new RuntimeException(TAG + ": 数据源不允许为空");
        }
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.multiple = false;
        this.listData = listData;
        this.selectedList = null;
        this.noneSelectedList = null;
    }

    public ContactsSearchListAdapter(Context context, ArrayList<ContactData> listData, LinkedList<ContactData> selectedList, LinkedList<ContactData> noneSelectedList) {
        if (listData == null || selectedList == null || noneSelectedList == null) {
            throw new RuntimeException(TAG + ": 数据源不允许为空");
        }
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.multiple = true;
        this.listData = listData;
        this.selectedList = selectedList;
        this.noneSelectedList = noneSelectedList;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public ContactData getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactData data = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_contacts_search_list_item, null);
            holder = new ViewHolder();
            holder.avatarImage = convertView.findViewById(R.id.avatarImage);
            holder.nicknameText = convertView.findViewById(R.id.nicknameText);
            holder.usernameText = convertView.findViewById(R.id.wxIdText);
            holder.checkBox = convertView.findViewById(R.id.checkBox);
            if (multiple) {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.onCheckedChangeListener = new OnCheckedChangeListener();
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (multiple) {
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(selectedList.contains(data));
            holder.onCheckedChangeListener.data = data;
            holder.checkBox.setOnCheckedChangeListener(holder.onCheckedChangeListener);
        }

        String nickname = data.conRemark.isEmpty() ? data.nickname : data.conRemark;
        holder.nicknameText.setText(nickname);
        holder.usernameText.setText("wxid_sldk239sti");
        return convertView;
    }

    public class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        public ContactData data;
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                selectedList.add(data);
                noneSelectedList.remove(data);
            }else {
                selectedList.remove(data);
                noneSelectedList.add(data);
            }
        }
    };

    private class ViewHolder {
        public ImageView avatarImage;
        public TextView nicknameText;
        public TextView usernameText;
        public CheckBox checkBox;
        public OnCheckedChangeListener onCheckedChangeListener;
    }
}
