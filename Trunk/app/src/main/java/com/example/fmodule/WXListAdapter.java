package com.example.fmodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class WXListAdapter extends BaseAdapter {

    private List<Map<String, Object>> list;
    private LayoutInflater inflater;
    private Context context;

    public WXListAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_main_wxlist_item, null);
            holder = new ViewHolder();
            holder.avatar = convertView.findViewById(R.id.avatarImage);
            holder.nickname = convertView.findViewById(R.id.nicknameText);
            holder.wxId = convertView.findViewById(R.id.wxIdText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Map map = list.get(position);
        byte[] avatarBytes = Base64.decode((String)map.get("avatarBase64"), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
        //Drawable avatarDrawable = new BitmapDrawable(context.getResources(), bitmap);
        holder.avatar.setImageBitmap(bitmap);
        holder.nickname.setText((String)map.get("nickname"));
        holder.wxId.setText((String)map.get("wxId"));

        return convertView;
    }

    private class ViewHolder {
        public ImageView avatar;
        public TextView nickname;
        public TextView wxId;
    }
}
