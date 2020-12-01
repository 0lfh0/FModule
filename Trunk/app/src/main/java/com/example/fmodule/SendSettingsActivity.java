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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.hooktask.ContactSendTask;
import com.example.fmodule.hooktask.HookTask;
import com.example.fmodule.hooktask.HookTaskHelper;
import com.example.fmodule.hooktask.SendTask;
import com.example.fmodule.sqlite.SQLiteHelper;
import com.example.pickdatetime.DatePickDialog;
import com.example.pickdatetime.OnSureListener;
import com.example.pickdatetime.bean.DateParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SendSettingsActivity extends AppCompatActivity {

    private String wxId;
    private boolean isMulti;
    private ArrayList<String> toUsers;

    private SendTask sendTask;
    private SQLiteDatabase db;

    private TextView selectContactsText;
    private EditText sendMsgEdit;
    private ViewGroup sendMsgLLayout;
    private EditText serverApiEdit;
    private ViewGroup serverApiCLayout;

    private ViewGroup emojiCLayout;
    private EditText emojiUrlEdit;

    private Spinner sendModeSpinner;
    private Spinner sendTimeModeSpinner;
    private TextView sendTimeText;
    private Date sendTime;

    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_settings);
        setTitle("发送设置");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        String contactWxId = intent.getStringExtra("contactWxId");
        isMulti = intent.getBooleanExtra("multiple", false);
        if (isMulti) {
            toUsers = new ArrayList<>();
            int toUserCount = intent.getIntExtra("toUserCount", 0);
            for (int i=0; i<toUserCount; i++) {
                toUsers.add(intent.getStringExtra("toUserItem-" + i));
            }
            findViewById(R.id.selectContactsCLayout).setVisibility(View.VISIBLE);
            selectContactsText = findViewById(R.id.selectContactsText);
            selectContactsText.setText("已选择" + toUsers.size() + "位联系人");
            findViewById(R.id.selectContactsBtn).setOnClickListener(selectContactsBtnClickListener);
        }

        sendTask = new SendTask();
        sendTask.id = intent.getIntExtra("id", -1);
        sendTask.wxId = wxId;
        sendTask.toUser = contactWxId;

        //发送内容编辑框
        sendMsgEdit = findViewById(R.id.sendMsgEdit);
        sendMsgLLayout = findViewById(R.id.sendMsgLLayout);
        sendMsgLLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgEdit.requestFocus();
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
        //发送模式下拉框
        sendModeSpinner = findViewById(R.id.sendModeSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"固定文本", "表情图片", "远程接口"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sendModeSpinner.setAdapter(adapter);
        sendModeSpinner.setOnItemSelectedListener(sendModeSpinnerListener);
        //发送时间模式下拉框
        sendTimeModeSpinner = findViewById(R.id.sendTimeModeSpinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"5秒后", "30秒后", "1分钟后", "自定义"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sendTimeModeSpinner.setAdapter(adapter);
        sendTimeModeSpinner.setOnItemSelectedListener(sendTimeModeSpinnerListener);
        //时间选择文本
        sendTimeText = findViewById(R.id.sendTimeText);
        sendTimeText.setOnClickListener(sendTimeTextClickListener);
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != 1 || resultCode != 1) {
            return;
        }
        toUsers.clear();
        int selectedCount = data.getIntExtra("selectedCount", 0);
        for (int i=0; i<selectedCount; i++) {
            toUsers.add(data.getStringExtra("selectedItem-" + i));
        }
        selectContactsText.setText("已选择" + toUsers.size() + "位联系人");
        super.onActivityResult(requestCode, resultCode, data);
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
        Cursor cursor = db.query(SQLiteHelper.tableSendTask, null, "id=?", new String[]{String.valueOf(sendTask.id)}, null, null, null);
        if (cursor.moveToNext()) {
            sendTask.convertFrom(cursor);
        }
        cursor.close();
        sendMsgLLayout.setVisibility(sendTask.sendMode == SendTask.SendMode.fixedText ? View.VISIBLE : View.GONE);
        emojiCLayout.setVisibility(sendTask.sendMode == SendTask.SendMode.emojiUrl ? View.VISIBLE : View.GONE);
        serverApiCLayout.setVisibility(sendTask.sendMode == SendTask.SendMode.serverApi ? View.VISIBLE : View.GONE);
        switch (sendTask.sendMode) {
            case fixedText:
                sendModeSpinner.setSelection(0, true);
                sendMsgEdit.setText(sendTask.sendMsg);
                break;
            case emojiUrl:
                sendModeSpinner.setSelection(1, true);
                emojiUrlEdit.setText(sendTask.emojiUrl);
                break;
            case serverApi:
                sendModeSpinner.setSelection(2, true);
                serverApiEdit.setText(sendTask.serverApi);
                break;
        }

        switch (sendTask.sendTimeMode) {
            case custom:
                sendTimeText.setVisibility(View.VISIBLE);
                String timeText = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(sendTask.sendDate);
                sendTimeText.setText(timeText);
                break;
            default:
                sendTimeText.setVisibility(View.GONE);
                break;
        }
        sendTimeModeSpinner.setSelection(sendTask.sendTimeMode.ordinal(), true);
        //保存按钮
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(saveBtnClickListener);
    }
    //点击选择联系人
    private View.OnClickListener selectContactsBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(SendSettingsActivity.this, ContactsSearchActivity.class);
            intent.putExtra("wxId", wxId);
            intent.putExtra("multiple", true);
            intent.putExtra("selectedCount", toUsers.size());
            for (int i=0; i<toUsers.size(); i++) {
                intent.putExtra("selectedItem-" + i, toUsers.get(i));
            }
            startActivityForResult(intent, 1);
        }
    };
    //发送模式下拉框的事件监听器
    private AdapterView.OnItemSelectedListener sendModeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sendMsgLLayout.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            emojiCLayout.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            serverApiCLayout.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    //发送时间模式下拉框的事件监听器
    private AdapterView.OnItemSelectedListener sendTimeModeSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            sendTime = null;
            if (position < 3) {
                sendTimeText.setVisibility(View.GONE);
            } else {
                sendTimeText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    //选择时间
    private View.OnClickListener sendTimeTextClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar startCal = Calendar.getInstance();
            startCal.add(Calendar.MINUTE, 1);
            Calendar endCal = Calendar.getInstance();
            endCal.add(Calendar.YEAR, 6);

            new DatePickDialog.Builder()
                    .setTypes(DateParams.TYPE_YEAR, DateParams.TYPE_MONTH, DateParams.TYPE_DAY, DateParams.TYPE_HOUR, DateParams.TYPE_MINUTE)
                    .setCurrentDate(startCal.getTime())
                    .setStartDate(startCal.getTime())
                    .setEndDate(endCal.getTime())
                    .setOnSureListener(new OnSureListener() {
                        @Override
                        public void onSure(Date date) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            date = calendar.getTime();
                            String message = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
                            sendTimeText.setText(message);
                            sendTime = date;
                        }
                    })
                    .show(SendSettingsActivity.this);
        }
    };
    //验证表单
    private boolean validateForm() {
        if (isMulti && toUsers.size() == 0) {
            Toast.makeText(this, "请选择联系人", Toast.LENGTH_SHORT).show();
            return false;
        }

        int sendModeSelectedIndex = sendModeSpinner.getSelectedItemPosition();
        if (sendModeSelectedIndex == 0 && sendMsgEdit.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入需要发送的内容", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sendModeSelectedIndex == 1 && emojiUrlEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入表情链接地址", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sendModeSelectedIndex == 2 && serverApiEdit.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入远程接口地址", Toast.LENGTH_SHORT).show();
            return false;
        }

        int sendTimeModeSelectedIndex = sendTimeModeSpinner.getSelectedItemPosition();
        if (sendTimeModeSelectedIndex == 3) {
            if (sendTime == null) {
                Toast.makeText(this, "请选择时间", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (sendTime.getTime() <= new Date().getTime()) {
                Toast.makeText(this, "选择的时间要晚于当前时间", Toast.LENGTH_SHORT).show();
                return false;
            }
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

            //设置发送内容参数
            int sendModeSelectedIndex = sendModeSpinner.getSelectedItemPosition();
            sendTask.sendMode = SendTask.SendMode.values()[sendModeSelectedIndex];
            if (sendModeSelectedIndex == 0) {
                sendTask.sendMsg = sendMsgEdit.getText().toString();
            } else if (sendModeSelectedIndex == 1) {
                sendTask.emojiUrl = emojiUrlEdit.getText().toString();
            } else if (sendModeSelectedIndex == 2) {
                sendTask.serverApi = serverApiEdit.getText().toString();
            }
            //设置发送时间参数
            int sendTimeModeSelectedIndex = sendTimeModeSpinner.getSelectedItemPosition();
            sendTask.sendTimeMode = SendTask.SendTimeMode.values()[sendTimeModeSelectedIndex];
            switch (sendTask.sendTimeMode) {
                case timeout_5s:
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 5);
                    sendTask.sendDate = calendar.getTime();
                    break;
                case timeout_30s:
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, 30);
                    sendTask.sendDate = calendar.getTime();
                    break;
                case timeout_1m:
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, 1);
                    sendTask.sendDate = calendar.getTime();
                    break;
                case custom:
                    sendTask.sendDate = sendTime;
                    break;
            }

            sendTask.toUsers = null;
            if (isMulti) {
                sendTask.toUsers = "";
                for (String s : toUsers) {
                    sendTask.toUsers += s + ":";
                }
            }
            //保存SendTask
            ContentValues values = new ContentValues();
            values.put("wxId", sendTask.wxId);
            values.put("toUser", sendTask.toUser);
            values.put("toUsers", sendTask.toUsers);
            values.put("sendMode", sendTask.sendMode.toString());
            values.put("sendMsg", sendTask.sendMsg);
            values.put("emojiUrl", sendTask.emojiUrl);
            values.put("serverApi", sendTask.serverApi);
            values.put("sendTimeMode", sendTask.sendTimeMode.toString());
            values.put("sendDate", sendTask.sendDate.getTime());
            int eCount = db.update(SQLiteHelper.tableSendTask, values, "id=?", new String[]{String.valueOf(sendTask.id)});
            if (eCount == 0) {
                db.insert(SQLiteHelper.tableSendTask, null, values);
                Cursor cursor = db.rawQuery("select last_insert_rowid() from " + SQLiteHelper.tableSendTask, null);
                cursor.moveToFirst();
                sendTask.id = cursor.getInt(0);
                cursor.close();
            }

            mService.createSendTaskTimer(sendTask);
            Toast.makeText(SendSettingsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        }
    };
}