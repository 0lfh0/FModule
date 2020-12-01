package com.example.fmodule;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.hooktask.ReplyTask;
import com.example.fmodule.message.hooktask.MReplyTask;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

public class ReplySettingsActivity extends AppCompatActivity {
    private ReplyTask replyTask;
    private SQLiteDatabase db;

    private EditText patternMsgEdit;
    private EditText replyMsgEdit;
    private ViewGroup patternMsgLLayout;
    private ViewGroup replyMsgLLayout;
    private ViewGroup serverApiCLayout;
    private EditText serverApiEdit;
    private Spinner patternModeSpinner;
    private Spinner replyModeSpinner;

    private ViewGroup emojiCLayout;
    private EditText emojiUrlEdit;

    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_settings);
        setTitle("回复设置");
        Intent intent = getIntent();
        replyTask = new ReplyTask();
        replyTask.id = intent.getIntExtra("id", -1);
        replyTask.wxId = intent.getStringExtra("wxId");
        replyTask.fromUser = intent.getStringExtra("contactWxId");

        //匹配消息编辑框
        patternMsgEdit = findViewById(R.id.patternMsgEdit);
        patternMsgLLayout = findViewById(R.id.patternMsgLLayout);
        patternMsgLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patternMsgEdit.requestFocus();
            }
        });
        //回复内容编辑框
        replyMsgEdit = findViewById(R.id.replyMsgEdit);
        replyMsgLLayout = findViewById(R.id.replyMsgLLayout);
        replyMsgLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyMsgEdit.requestFocus();
            }
        });
        //远程接口内容
        serverApiCLayout = findViewById(R.id.serverApiCLayout);
        ViewGroup serverApiLLayout = findViewById(R.id.serverApiLLayout);
        serverApiEdit = findViewById(R.id.serverApiEdit);
        serverApiLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverApiEdit.requestFocus();
            }
        });
        //表情包地址内容
        emojiCLayout = findViewById(R.id.emojiCLayout);
        ViewGroup emojiLLayout = findViewById(R.id.emojiLLayout);
        emojiUrlEdit = findViewById(R.id.emojiUrlEdit);
        emojiLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiUrlEdit.requestFocus();
            }
        });
        //匹配模式下拉框
        patternModeSpinner = findViewById(R.id.patternModeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"模糊", "精确", "全部"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patternModeSpinner.setAdapter(adapter);
        patternModeSpinner.setOnItemSelectedListener(patternModeSpinnerListener);
        //回复模式下拉框
        replyModeSpinner = findViewById(R.id.replyModeSpinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"固定文本", "表情图片", "远程接口"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        replyModeSpinner.setAdapter(adapter);
        replyModeSpinner.setOnItemSelectedListener(replyModeSpinnerListener);
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        unbindService(conn);
        mService = null;
        super.onDestroy();
    }
    //绑定服务回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            db = mService.getSQLiteHelper().getWritableDatabase();
            initializeUi();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    //初始化ui
    private void initializeUi() {
        //从数据库中加载数据
        Cursor cursor = db.query(SQLiteHelper.tableReplyTask, null, "id=?", new String[]{String.valueOf(replyTask.id)}, null, null, null);
        if (cursor.moveToNext()) {
            replyTask.convertFrom(cursor);
        }
        cursor.close();
        switch (replyTask.patternMode) {
            case fuzzy:
                patternModeSpinner.setSelection(0, true);
                patternMsgEdit.setText(replyTask.patternMsg);
                break;
            case precise:
                patternModeSpinner.setSelection(1, true);
                patternMsgEdit.setText(replyTask.patternMsg);
                break;
            case all:
                patternModeSpinner.setSelection(2, true);
                patternMsgLLayout.setVisibility(View.GONE);
                break;
        }

        replyMsgLLayout.setVisibility(replyTask.replyMode == ReplyTask.ReplyMode.fixedText ? View.VISIBLE : View.GONE);
        emojiCLayout.setVisibility(replyTask.replyMode == ReplyTask.ReplyMode.emojiUrl ? View.VISIBLE : View.GONE);
        serverApiCLayout.setVisibility(replyTask.replyMode == ReplyTask.ReplyMode.serverApi ? View.VISIBLE : View.GONE);
        switch (replyTask.replyMode) {
            case fixedText:
                replyModeSpinner.setSelection(0, true);
                replyMsgEdit.setText(replyTask.replyMsg);
                break;
            case emojiUrl:
                replyModeSpinner.setSelection(1, true);
                emojiUrlEdit.setText(replyTask.emojiUrl);
                break;
            case serverApi:
                replyModeSpinner.setSelection(2, true);
                serverApiEdit.setText(replyTask.serverApi);
                break;
        }

        //保存按钮
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(saveBtnClickListener);
    }
    //匹配模式下拉框的事件监听器
    private AdapterView.OnItemSelectedListener patternModeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            patternMsgLLayout.setVisibility(position == 0 || position == 1 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(ReplySettingsActivity.this, "请选择匹配模式", Toast.LENGTH_SHORT).show();
        }
    };
    //回复模式下拉框的事件监听器
    private AdapterView.OnItemSelectedListener replyModeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            replyMsgLLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            emojiCLayout.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            serverApiCLayout.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //验证表单
    private boolean validateForm() {
        int patternModeSelectedIndex = patternModeSpinner.getSelectedItemPosition();
        if ((patternModeSelectedIndex == 0 || patternModeSelectedIndex == 1) && patternMsgEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入需要匹配的消息", Toast.LENGTH_SHORT).show();
            return false;
        }

        int replyModeSelectedIndex = replyModeSpinner.getSelectedItemPosition();
        if (replyModeSelectedIndex == 0 && replyMsgEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入回复内容", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (replyModeSelectedIndex == 1 && emojiUrlEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入表情链接地址", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (replyModeSelectedIndex == 2 && serverApiEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入远程接口地址", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    //点击保存
    private View.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!validateForm()) {
                return;
            }

            //设置匹配参数
            int patternModeSelectedIndex = patternModeSpinner.getSelectedItemPosition();
            if (patternModeSelectedIndex == 0 || patternModeSelectedIndex == 1) {
                replyTask.patternMsg = patternMsgEdit.getText().toString();
            }
            replyTask.patternMode = ReplyTask.PatternMode.values()[patternModeSelectedIndex];
            //设置回复参数
            int replyModeSelectedIndex = replyModeSpinner.getSelectedItemPosition();
            if (replyModeSelectedIndex == 0) {
                replyTask.replyMsg = replyMsgEdit.getText().toString();
            } else if (replyModeSelectedIndex == 1){
                replyTask.emojiUrl = emojiUrlEdit.getText().toString();
            } else {
                replyTask.serverApi = serverApiEdit.getText().toString();
            }
            replyTask.replyMode = ReplyTask.ReplyMode.values()[replyModeSelectedIndex];

            //保存任务
            ContentValues values = new ContentValues();
            values.put("wxId", replyTask.wxId);
            values.put("fromUser", replyTask.fromUser);
            values.put("isOn", replyTask.isOn ? 1 : 0);
            values.put("patternMode", replyTask.patternMode.toString());
            values.put("patternMsg", replyTask.patternMsg);
            values.put("replyMode", replyTask.replyMode.toString());
            values.put("replyMsg", replyTask.replyMsg);
            values.put("emojiUrl", replyTask.emojiUrl);
            values.put("serverApi", replyTask.serverApi);
            int eCount = db.update(SQLiteHelper.tableReplyTask, values, "id=?", new String[]{String.valueOf(replyTask.id)});
            if (eCount == 0) {
                db.insert(SQLiteHelper.tableReplyTask, null, values);
                Cursor cursor = db.rawQuery("select last_insert_rowid() from" + SQLiteHelper.tableReplyTask, null);
                cursor.moveToFirst();
                replyTask.id = cursor.getInt(0);
                cursor.close();
            }
            //同步微信端
            MReplyTask mReplyTask = new MReplyTask();
            mReplyTask.opType = eCount == 0? MReplyTask.OpType.add : MReplyTask.OpType.update;
            mReplyTask.replyTask = replyTask;
            mService.getWxSession(replyTask.wxId).send(mReplyTask);

            Toast.makeText(ReplySettingsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        }
    };
}