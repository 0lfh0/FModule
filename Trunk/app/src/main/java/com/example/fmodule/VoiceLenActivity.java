package com.example.fmodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

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

import com.example.fmodule.hooktask.VoiceLenTask;
import com.example.fmodule.message.hooktask.MDiceTask;
import com.example.fmodule.message.hooktask.MVoiceLenTask;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import easynet.network.Session;

public class VoiceLenActivity extends AppCompatActivity {
    private String wxId;
    private Button saveBtn;
    private EditText numEText;
    private SQLiteDatabase db;
    private VoiceLenTask voiceLenTask;
    private Session session;
    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_len);
        setTitle("固定语音秒数");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        voiceLenTask = new VoiceLenTask();
        voiceLenTask.wxId = wxId;
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
        Cursor cursor = db.query(SQLiteHelper.tableVoiceLenTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        if (cursor.moveToFirst()) {
            voiceLenTask.convertFrom(cursor);
        }
        SwitchCompat voiceLenSwitch = (SwitchCompat)findViewById(R.id.voiceLenSwitch);
        voiceLenSwitch.setChecked(voiceLenTask.isOn);
        voiceLenSwitch.setOnCheckedChangeListener(onCheckedChangeListener);

        numEText = (EditText) findViewById(R.id.voiceLenEdit);
        numEText.setText(String.valueOf(voiceLenTask.number));
        saveBtn = (Button) findViewById(R.id.saveBtn);
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
    //保存任务
    private void saveVoiceLenTask() {
        ContentValues values = new ContentValues();
        values.put("wxId", voiceLenTask.wxId);
        values.put("isOn", voiceLenTask.isOn);
        values.put("number", voiceLenTask.number);
        int eCount = db.update(SQLiteHelper.tableVoiceLenTask, values, "wxId=?", new String[]{voiceLenTask.wxId});
        if (eCount == 0) {
            db.insert(SQLiteHelper.tableVoiceLenTask, null, values);
        }
        MVoiceLenTask message = new MVoiceLenTask();
        message.task = voiceLenTask;
        session.send(message);
    }
    //开关
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            voiceLenTask.isOn = isChecked;
            saveVoiceLenTask();
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
                    Toast.makeText(VoiceLenActivity.this, "秒数不允许为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int number = Integer.parseInt(str);
                if (number < 1 || number > 60) {
                    Toast.makeText(VoiceLenActivity.this, "只能在1秒和60秒之间", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveBtn.setText("编辑");
                isEditing = false;
                numEText.setClickable(false);
                numEText.clearFocus();
                numEText.setSelected(false);
                numEText.setFocusable(false);
                numEText.setFocusableInTouchMode(false);

                voiceLenTask.number = number;
                saveVoiceLenTask();
                Toast.makeText(VoiceLenActivity.this, "保存完成", Toast.LENGTH_SHORT).show();
            }
        }
    };
}