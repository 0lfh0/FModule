package com.example.fmodule;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.eventsystem.EventHandler;
import com.example.fmodule.eventsystem.EventIdType;
import com.example.fmodule.eventsystem.EventSystem;

public class SendActivity extends AppCompatActivity {

    private String wxId;
    private String nickname;
    private String contactWxId;
    private String contactNickname;
    private String contactConRemark;
    private ListView listView;
    private SendListAdapter listAdapter;
    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        setTitle("自动发送");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        nickname = intent.getStringExtra("nickname");
        contactWxId = intent.getStringExtra("contactWxId");
        contactNickname = intent.getStringExtra("contactNickname");
        contactConRemark = intent.getStringExtra("contactConRemark");
        listView = findViewById(R.id.sendList);
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);

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
        getMenuInflater().inflate(R.menu.activity_send, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(SendActivity.this, SendSettingsActivity.class);
            intent.putExtra("wxId", wxId);
            intent.putExtra("nickname", nickname);
            intent.putExtra("contactWxId", contactWxId);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        EventSystem.getInstance().unregister(this);
        unbindService(conn);
        mService = null;
        super.onDestroy();
    }
    @EventHandler(EventIdType.sendTaskCompleted)
    public void sendTaskCompletedHandler(int sendTaskId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter.refreshData();
            }
        });
    }
    //绑定服务回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            SQLiteDatabase db = mService.getSQLiteHelper().getWritableDatabase();
            listAdapter = new SendListAdapter(SendActivity.this, wxId, contactWxId, mService, db);
            listView.setAdapter(listAdapter);
            EventSystem.getInstance().register(SendActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
}