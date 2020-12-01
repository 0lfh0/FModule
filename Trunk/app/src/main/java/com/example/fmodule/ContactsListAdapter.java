package com.example.fmodule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmodule.message.useravatar.PUserAvatar;
import com.example.fmodule.message.useravatar.QUserAvatar;
import com.example.fmodule.other.ContactData;
import com.example.fmodule.other.NetHelper;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import easynet.network.Session;

public class ContactsListAdapter extends BaseAdapter {

    private Context context;
    private String wxId;
    private LinkedList<ContactData> contacts;
    private LayoutInflater inflater;
    private final Hashtable<String, Bitmap> avatarCache = new Hashtable<>();
    private Session session;

    public ContactsListAdapter(Context context, String wxId, LinkedList<ContactData> contacts, Session session) {
        this.context = context;
        this.wxId = wxId;
        this.contacts = contacts;
        this.inflater = LayoutInflater.from(context);
        this.session = session;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactData data = (ContactData)getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_contacts_list_item, null);
            holder = new ViewHolder();
            holder.avatarImage = convertView.findViewById(R.id.avatarImage);
            holder.nicknameText = convertView.findViewById(R.id.nicknameText);
            holder.usernameText = convertView.findViewById(R.id.wxIdText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.username = data.username;
        String nickname = data.conRemark.isEmpty() ? data.nickname : data.conRemark;
        holder.nicknameText.setText(nickname);
        holder.usernameText.setText("wxid_tit23hifaf");
        setAvatar(holder);
        return convertView;
    }

    private class ViewHolder {
        public String username;
        public ImageView avatarImage;
        public TextView nicknameText;
        public TextView usernameText;
    }

    private void setAvatar(ViewHolder holder) {
        String username = holder.username;
        Bitmap bitmap = avatarCache.get(username);
        if (bitmap != null) {
            holder.avatarImage.setImageBitmap(bitmap);
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                QUserAvatar qUserAvatar = new QUserAvatar();
                qUserAvatar.wxId = username;
                PUserAvatar pUserAvatar = (PUserAvatar) session.call(qUserAvatar).execute();
                if (pUserAvatar == null) {
                    return;
                }
                byte[] bytes = pUserAvatar.avatarData;
                if (bytes == null) {
                    pUserAvatar = (PUserAvatar) session.call(qUserAvatar).execute();
                    if (pUserAvatar == null) {
                        return;
                    }
                    bytes = pUserAvatar.avatarData;
                    if (bytes == null) {
                        return;
                    }
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                avatarCache.put(username, bmp);

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (holder.username == username) {
                            holder.avatarImage.setImageBitmap(bmp);
                        }
                    }
                });
            }
        }).start();
    }
}
