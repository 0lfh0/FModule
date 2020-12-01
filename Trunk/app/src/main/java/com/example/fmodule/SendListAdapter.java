package com.example.fmodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fmodule.hooktask.HookTaskHelper;
import com.example.fmodule.hooktask.SendTask;
import com.example.fmodule.sqlite.SQLiteHelper;

import java.text.SimpleDateFormat;

public class SendListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String wxId;
    private String contactWxId;
    private Cursor cursor;
    private SQLiteDatabase db;
    private MService mService;

    public SendListAdapter(Context context, String wxId, String contactWxId, MService mService, SQLiteDatabase db) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.wxId = wxId;
        this.contactWxId = contactWxId;
        this.mService = mService;
        this.db = db;
        refreshDataWithoutNotify();
    }

    public void refreshData() {
        refreshDataWithoutNotify();
        notifyDataSetChanged();
    }

    public void refreshDataWithoutNotify() {
        if (cursor != null) {
            cursor.close();
        }
        cursor = db.query(SQLiteHelper.tableSendTask, null, "wxId=? and toUser=?", new String[]{wxId, contactWxId}, null, null, null);
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public SendTask getItem(int position) {
        if (cursor == null) {
            return null;
        }
        cursor.moveToPosition(position);
        SendTask sendTask = new SendTask();
        sendTask.convertFrom(cursor);
        return sendTask;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_send_sendlist_item, parent, false);
            holder = new ViewHolder();
            holder.sendModeText = convertView.findViewById(R.id.sendModeText);
            holder.sendMsgCLayout = convertView.findViewById(R.id.sendMsgCLayout);
            holder.emojiCLayout = convertView.findViewById(R.id.emojiCLayout);
            holder.serverApiCLayout = convertView.findViewById(R.id.serverApiCLayout);
            holder.sendMsgText = convertView.findViewById(R.id.sendMsgText);
            holder.emojiUrlText = convertView.findViewById(R.id.emojiUrlText);
            holder.serverApiText = convertView.findViewById(R.id.serverApiText);
            holder.sendTimeText = convertView.findViewById(R.id.sendTimeText);

            holder.editBtn = convertView.findViewById(R.id.editBtn);
            holder.deleteBtn = convertView.findViewById(R.id.deleteBtn);
            holder.editBtnClickListener = new EditBtnClickListener();
            holder.editBtn.setOnClickListener(holder.editBtnClickListener);
            holder.deleteBtnClickListener = new DeleteBtnClickListener();
            holder.deleteBtn.setOnClickListener(holder.deleteBtnClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SendTask data = getItem(position);
        //设置发送内容
        holder.sendMsgCLayout.setVisibility(data.sendMode == SendTask.SendMode.fixedText ? View.VISIBLE : View.GONE);
        holder.emojiCLayout.setVisibility(data.sendMode == SendTask.SendMode.emojiUrl ? View.VISIBLE : View.GONE);
        holder.serverApiCLayout.setVisibility(data.sendMode == SendTask.SendMode.serverApi ? View.VISIBLE : View.GONE);
        switch (data.sendMode) {
            case fixedText:
                holder.sendModeText.setText("固定文本");
                holder.sendMsgText.setText(data.sendMsg);
                break;
            case emojiUrl:
                holder.sendModeText.setText("表情图片");
                holder.emojiUrlText.setText(data.emojiUrl);
                break;
            case serverApi:
                holder.sendModeText.setText("远程接口");
                holder.serverApiText.setText(data.serverApi);
                break;
        }
        //设置时间
        switch (data.sendTimeMode)
        {
            case timeout_5s:
                holder.sendTimeText.setText("5秒后");
                break;
            case timeout_30s:
                holder.sendTimeText.setText("30秒后");
                break;
            case timeout_1m:
                holder.sendTimeText.setText("1分钟后");
                break;
            case custom:
                String timeText = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data.sendDate);
                holder.sendTimeText.setText(timeText);
                break;
        }
        holder.editBtnClickListener.data = data;
        holder.deleteBtnClickListener.data = data;

        return convertView;
    }

    private class ViewHolder {
        public TextView sendModeText;
        public ViewGroup sendMsgCLayout;
        public ViewGroup emojiCLayout;
        public ViewGroup serverApiCLayout;
        public TextView sendMsgText;
        public TextView emojiUrlText;
        public TextView serverApiText;
        public TextView sendTimeText;

        public ImageButton editBtn;
        public ImageButton deleteBtn;
        public EditBtnClickListener editBtnClickListener;
        public DeleteBtnClickListener deleteBtnClickListener;
    }

    private class EditBtnClickListener implements View.OnClickListener {
        public SendTask data;
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SendSettingsActivity.class);
            intent.putExtra("id", data.id);
            intent.putExtra("wxId", data.wxId);
            intent.putExtra("contactWxId", data.toUser);
            boolean isMulti = data.toUsers != null;
            intent.putExtra("multiple", isMulti);
            if (isMulti) {
                String[] toUsers = data.toUsers.split(":");
                intent.putExtra("toUserCount", toUsers.length);
                for (int i=0; i<toUsers.length; i++) {
                    intent.putExtra("toUserItem-" + i, toUsers[i]);
                }
            }
            ((Activity)context).startActivityForResult(intent, 1);
        }
    }

    private class DeleteBtnClickListener implements View.OnClickListener {
        public SendTask data;
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setPositiveButton(
                    "确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (data == null) {
                                return;
                            }
                            int count = db.delete(SQLiteHelper.tableSendTask, "id=?", new String[]{String.valueOf(data.id)});
                            if (count == 0) {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            refreshData();
                            mService.cancelSendTaskTimer(data.id);
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            builder.setNegativeButton(
                    "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            builder.setMessage("确定要删除该项回复设置吗？");
            builder.setTitle("提示");
            builder.show();
        }
    }
}
