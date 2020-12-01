package com.example.fmodule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.example.fmodule.hooktask.ReplyTask;
import com.example.fmodule.hooktask.SendTask;
import com.example.fmodule.message.hooktask.MReplyTask;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import easynet.network.Session;

public class ReplyListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    private String wxId;
    private String contactWxId;

    private Session session;

    public ReplyListAdapter(Context context, String wxId, String contactWxId, SQLiteDatabase db, Session session) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.wxId = wxId;
        this.contactWxId = contactWxId;
        this.db = db;
        this.session = session;
        refreshDataWithoutNotify();
    }

    public void refreshData() {
        refreshDataWithoutNotify();
        notifyDataSetChanged();
    }

    public void refreshDataWithoutNotify() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = db.query(SQLiteHelper.tableReplyTask, null, "wxId=? and fromUser=?", new String[]{wxId, contactWxId}, null, null, null);
    }

    @Override
    public int getCount() {
        if (cursor == null) {
            return 0;
        }
        return cursor.getCount();
    }

    @Override
    public ReplyTask getItem(int position) {
        if (cursor == null) {
            return null;
        }
        cursor.moveToPosition(position);
        ReplyTask data = new ReplyTask();
        data.convertFrom(cursor);
        return data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_reply_replylist_item, parent, false);
            holder = new ViewHolder();
            //匹配设置
            holder.patternModeText = convertView.findViewById(R.id.patternModeText);
            holder.patternMsgCLayout = convertView.findViewById(R.id.patternMsgCLayout);
            holder.patternMsgText = convertView.findViewById(R.id.patternMsgText);
            //回复设置
            holder.replyModeText = convertView.findViewById(R.id.replyModeText);
            holder.replyMsgCLayout = convertView.findViewById(R.id.replyMsgCLayout);
            holder.replyMsgText = convertView.findViewById(R.id.replyMsgText);
            holder.emojiCLayout = convertView.findViewById(R.id.emojiCLayout);
            holder.emojiUrlText = convertView.findViewById(R.id.emojiUrlText);
            holder.serverApiCLayout = convertView.findViewById(R.id.serverApiCLayout);
            holder.serverApiText = convertView.findViewById(R.id.serverApiText);

            holder.itemSwitch = convertView.findViewById(R.id.itemSwitch);
            holder.editBtn = convertView.findViewById(R.id.editBtn);
            holder.deleteBtn = convertView.findViewById(R.id.deleteBtn);
            holder.onCheckedChangeListener = new OnCheckedChangeListener();
            holder.editBtnClickListener = new EditBtnClickListener();
            holder.editBtn.setOnClickListener(holder.editBtnClickListener);
            holder.deleteBtnClickListener = new DeleteBtnClickListener();
            holder.deleteBtn.setOnClickListener(holder.deleteBtnClickListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReplyTask data = getItem(position);
        //设置匹配内容
        switch (data.patternMode) {
            case precise:
                holder.patternModeText.setText("精确");
                holder.patternMsgCLayout.setVisibility(View.VISIBLE);
                holder.patternMsgText.setText(data.patternMsg);
                break;
            case fuzzy:
                holder.patternModeText.setText("模糊");
                holder.patternMsgCLayout.setVisibility(View.VISIBLE);
                holder.patternMsgText.setText(data.patternMsg);
                break;
            case all:
                holder.patternModeText.setText("全部");
                holder.patternMsgCLayout.setVisibility(View.GONE);
                break;
        }
        //设置回复内容
        holder.replyMsgCLayout.setVisibility(data.replyMode == ReplyTask.ReplyMode.fixedText ? View.VISIBLE : View.GONE);
        holder.emojiCLayout.setVisibility(data.replyMode == ReplyTask.ReplyMode.emojiUrl ? View.VISIBLE : View.GONE);
        holder.serverApiCLayout.setVisibility(data.replyMode == ReplyTask.ReplyMode.serverApi ? View.VISIBLE : View.GONE);
        switch (data.replyMode) {
            case fixedText:
                holder.replyModeText.setText("固定文本");
                holder.replyMsgText.setText(data.replyMsg);
                break;
            case emojiUrl:
                holder.replyModeText.setText("表情图片");
                holder.emojiUrlText.setText(data.emojiUrl);
                break;
            case serverApi:
                holder.replyModeText.setText("远程接口");
                holder.serverApiText.setText(data.serverApi);
                break;
        }
        holder.itemSwitch.setOnCheckedChangeListener(null);
        holder.itemSwitch.setChecked(data.isOn);
        holder.onCheckedChangeListener.data = data;
        holder.itemSwitch.setOnCheckedChangeListener(holder.onCheckedChangeListener);
        holder.editBtnClickListener.data = data;
        holder.deleteBtnClickListener.data = data;

        return convertView;
    }

    private class ViewHolder {
        //匹配设置
        public TextView patternModeText;
        public ViewGroup patternMsgCLayout;
        public TextView patternMsgText;
        //回复设置
        public TextView replyModeText;
        public ViewGroup replyMsgCLayout;
        public TextView replyMsgText;
        public ViewGroup emojiCLayout;
        public TextView emojiUrlText;
        public ViewGroup serverApiCLayout;
        public TextView serverApiText;

        public SwitchCompat itemSwitch;
        public ImageButton editBtn;
        public ImageButton deleteBtn;
        public OnCheckedChangeListener onCheckedChangeListener;
        public EditBtnClickListener editBtnClickListener;
        public DeleteBtnClickListener deleteBtnClickListener;
    }

    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        public ReplyTask data;
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            data.isOn = isChecked;
            ContentValues values = new ContentValues();
            values.put("isOn", isChecked ? 1 : 0);
            db.update(SQLiteHelper.tableReplyTask, values, "id=?", new String[]{String.valueOf(data.id)});
            MReplyTask mReplyTask = new MReplyTask();
            mReplyTask.replyTask = data;
            session.send(mReplyTask);
        }
    }

    private class EditBtnClickListener implements View.OnClickListener {
        public ReplyTask data;
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ReplySettingsActivity.class);
            intent.putExtra("id", data.id);
            intent.putExtra("wxId", data.wxId);
            intent.putExtra("contactWxId", data.fromUser);
            ((Activity)context).startActivityForResult(intent, 1);
        }
    }

    private class DeleteBtnClickListener implements View.OnClickListener {
        public ReplyTask data;
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
                            int eCount = db.delete(SQLiteHelper.tableReplyTask, "id=?", new String[]{String.valueOf(data.id)});
                            if (eCount == 0) {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            refreshData();
                            MReplyTask mReplyTask = new MReplyTask();
                            mReplyTask.opType = MReplyTask.OpType.delete;
                            mReplyTask.replyTask = data;
                            session.send(mReplyTask);
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
