package com.example.fmodule;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.example.fmodule.eventsystem.EventHandler;
import com.example.fmodule.eventsystem.EventIdType;
import com.example.fmodule.eventsystem.EventSystem;
import com.example.fmodule.hooktask.DiceTask;
import com.example.fmodule.hooktask.HookTaskHelper;
import com.example.fmodule.hooktask.ReplyTask;
import com.example.fmodule.hooktask.SendTask;
import com.example.fmodule.hooktask.VoiceLenTask;
import com.example.fmodule.message.MessageManifest;
import com.example.fmodule.message.hooktask.MDiceTask;
import com.example.fmodule.message.hooktask.MReplyTask;
import com.example.fmodule.message.hooktask.MVoiceLenTask;
import com.example.fmodule.message.identify.PIdentify;
import com.example.fmodule.message.identify.QIdentify;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import easynet.network.MessageHandler;
import easynet.network.Network;
import easynet.network.Session;

public class MainActivity extends AppCompatActivity {

    private static MHandler mHandler;
    private static SQLiteHelper mSQLiteHelper;

    private ArrayList<Map<String, Object>> infoList;
    private final Hashtable<Session, Map<String, Object>> sessionInfo = new Hashtable<>();
    private WXListAdapter adapter;
    private ListView infoListView;
    private Group noSessionTipsGroup;
    private MService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("已连接");
        mHandler = new MHandler(MainActivity.this);

        infoListView = findViewById(R.id.listView);
        infoListView.setOnItemClickListener(onWxListItemClickListener);
        noSessionTipsGroup = (Group)findViewById(R.id.noSessionTipsGroup);
        noSessionTipsGroup.setVisibility(View.VISIBLE);

        //创建绑定对象
        Intent intent = new Intent(this, MService.class);
        startService(intent);
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        EventSystem.getInstance().unregister(MainActivity.this);
        mService = null;
        unbindService(conn);
        super.onDestroy();
    }
    //绑定服务
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            infoList = mService.getInfoList();
            noSessionTipsGroup.setVisibility(infoList.size() == 0 ? View.VISIBLE : View.GONE);
            adapter = new WXListAdapter(MainActivity.this, infoList);
            infoListView.setAdapter(adapter);
            EventSystem.getInstance().register(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };
    @EventHandler(EventIdType.mServiceSessionChanged)
    public void mServiceSessionChangedHandler(String wxId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                noSessionTipsGroup.setVisibility(infoList.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }
    //点击条目
    private AdapterView.OnItemClickListener onWxListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
            Map<String, Object> data = infoList.get(position);
            intent.putExtra("nickname", (String)data.get("nickname"));
            intent.putExtra("wxId", (String)data.get("wxId"));
            startActivity(intent);
        }
    };

}