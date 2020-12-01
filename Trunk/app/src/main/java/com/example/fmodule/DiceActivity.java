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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.fmodule.hooktask.DiceTask;
import com.example.fmodule.message.hooktask.MDiceTask;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import easynet.network.Session;

public class DiceActivity extends AppCompatActivity {

    private String wxId;
    private Button saveBtn;
    private EditText numEText;
    private SQLiteDatabase db;
    private DiceTask diceTask;
    private Session session;
    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);
        setTitle("固定骰子点数");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        diceTask = new DiceTask();
        diceTask.wxId = wxId;
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }
    @Override
    protected void onDestroy() {
        session.unregisterErrorCallback(errorCallback);
        unbindService(conn);
        mService = null;
        super.onDestroy();
    }
    //初始化页面
    private void initializeUi() {
        Cursor cursor = db.query(SQLiteHelper.tableDiceTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        if (cursor.moveToFirst()) {
            diceTask.convertFrom(cursor);
        }
        SwitchCompat diceSwitch = (SwitchCompat)findViewById(R.id.diceSwitch);
        diceSwitch.setChecked(diceTask.isOn);
        diceSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        numEText = (EditText) findViewById(R.id.diceEText);
        if (diceTask.number < 1 || diceTask.number > 6) {
            diceTask.number = 1;
            saveDiceTask();
        }
        numEText.setText(String.valueOf(diceTask.number));
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(saveBtnClickListener);
    }
    //绑定服务回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            session = mService.getWxSession(wxId);
            session.registerErrorCallback(errorCallback);
            db = mService.getSQLiteHelper().getWritableDatabase();
            initializeUi();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    //session 异常回调
    private Session.ErrorCallback errorCallback = new Session.ErrorCallback() {
        @Override
        public void run(Session session, Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        }
    };
    //保存骰子任务
    private void saveDiceTask() {
        ContentValues values = new ContentValues();
        values.put("wxId", diceTask.wxId);
        values.put("isOn", diceTask.isOn);
        values.put("number", diceTask.number);
        int eCount = db.update(SQLiteHelper.tableDiceTask, values, "wxId=?", new String[]{diceTask.wxId});
        if (eCount == 0) {
            db.insert(SQLiteHelper.tableDiceTask, null, values);
        }
        MDiceTask message = new MDiceTask();
        message.diceTask = diceTask;
        session.send(message);
    }
    //任务开关
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            diceTask.isOn = isChecked;
            saveDiceTask();
        }
    };
    //点击保存
    private View.OnClickListener saveBtnClickListener = new View.OnClickListener(){
        private boolean isEditing = false;
        @Override
        public void onClick(View v) {
            if (!isEditing) {
                saveBtn.setText("保存");
                isEditing = true;
                numEText.setFocusable(true);
                numEText.setFocusableInTouchMode(true);
                numEText.requestFocus();
                numEText.setClickable(true);
            }else {
                String str = numEText.getText().toString().trim();
                if (str.isEmpty()) {
                    Toast.makeText(DiceActivity.this, "点数不允许为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveBtn.setText("编辑");
                isEditing = false;
                numEText.setClickable(false);
                numEText.clearFocus();
                numEText.setSelected(false);
                numEText.setFocusable(false);
                numEText.setFocusableInTouchMode(false);

                diceTask.number = Integer.parseInt(str);
                saveDiceTask();
                Toast.makeText(DiceActivity.this, "保存完成", Toast.LENGTH_SHORT).show();
            }
        }
    };
}