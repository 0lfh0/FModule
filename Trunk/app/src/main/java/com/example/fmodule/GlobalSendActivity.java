package com.example.fmodule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.fmodule.eventsystem.EventHandler;
import com.example.fmodule.eventsystem.EventIdType;
import com.example.fmodule.eventsystem.EventSystem;

import easynet.network.Session;

public class GlobalSendActivity extends AppCompatActivity {

    private String wxId;
    private ListView listView;
    private SendListAdapter listAdapter;
    private MService mService;
    private Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_send);
        setTitle("多选发送");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        listView = findViewById(R.id.sendList);
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
        EventSystem.getInstance().register(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || requestCode != 1) {
            return;
        }
        listAdapter.refreshData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_global_send, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(this, SendSettingsActivity.class);
            intent.putExtra("wxId", wxId);
            intent.putExtra("contactWxId", "all");
            intent.putExtra("multiple", true);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        unbindService(conn);
        mService = null;
        session.unregisterErrorCallback(errorCallback);
        EventSystem.getInstance().unregister(this);
        super.onDestroy();
    }
    //绑定服务回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            SQLiteDatabase db = mService.getSQLiteHelper().getWritableDatabase();
            session = mService.getWxSession(wxId);
            session.registerErrorCallback(errorCallback);
            listAdapter = new SendListAdapter(GlobalSendActivity.this, wxId, "all", mService, db);
            listView.setAdapter(listAdapter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    //session异常回调
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
    @EventHandler(EventIdType.sendTaskCompleted)
    public void sendTaskCompletedHandler(int sendTaskId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.refreshData();
            }
        });
    }
}