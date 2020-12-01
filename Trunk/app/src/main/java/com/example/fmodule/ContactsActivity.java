package com.example.fmodule;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.message.contacts.PContacts;
import com.example.fmodule.message.contacts.QContacts;
import com.example.fmodule.other.ContactData;
import com.example.fmodule.other.NetHelper;

import java.util.HashMap;
import java.util.LinkedList;

import easynet.network.Session;

public class ContactsActivity extends AppCompatActivity {

    private String wxId;
    private String nickname;
    private final LinkedList<ContactData> contacts = new LinkedList<>();
    private final HashMap<String, ContactData> contactsDict = new HashMap<>();
    private ContactsListAdapter adapter;
    private final MHandler mHandler = new MHandler(this);
    private Session session;

    private MService mService;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        nickname = intent.getStringExtra("nickname");
        setTitle("通讯录（" + nickname + "）");
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(itemClickListener);
        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1 || resultCode != 1) {
            return;
        }

        int selectedCount = data.getIntExtra("selectedCount", 0);
        if (selectedCount <= 0) {
            return;
        }
        String userId = data.getStringExtra("selectedItem-0");
        if (userId == null) {
            return;
        }
        ContactData cd = contactsDict.get(userId);
        if (cd == null) {
            return;
        }
        jumpToContactsProfile(cd);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.search:
                intent = new Intent(this, ContactsSearchActivity.class);
                intent.putExtra("wxId", wxId);
                intent.putExtra("multiple", false);
                startActivityForResult(intent, 1);
                break;
            case R.id.globalSend:
                intent = new Intent(this, GlobalSendActivity.class);
                intent.putExtra("wxId", wxId);
                startActivity(intent);
                break;
            case R.id.globalReply:
                intent = new Intent(this, GlobalReplyActivity.class);
                intent.putExtra("wxId", wxId);
                startActivity(intent);
                break;
            case R.id.globalDice:
                intent = new Intent(this, DiceActivity.class);
                intent.putExtra("wxId", wxId);
                startActivity(intent);
                break;
            case R.id.voiceLen:
                intent = new Intent(this, VoiceLenActivity.class);
                intent.putExtra("wxId", wxId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        session.unregisterErrorCallback(errorCallback);
        unbindService(conn);
        super.onDestroy();
    }
    //绑定服务
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            session = mService.getWxSession(wxId);
            session.registerErrorCallback(errorCallback);
            adapter = new ContactsListAdapter(ContactsActivity.this, wxId, contacts, session);
            listView.setAdapter(adapter);
            getContactsAsync();
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
    //异步获取通讯录
    private void getContactsAsync() {
        new Thread(new Runnable() {
            private PContacts cons = null;
            @Override
            public void run() {
                try {
                    cons = (PContacts) session.call(new QContacts()).execute();
                    for (ContactData cd : cons.contacts) {
                        contacts.offer(cd);
                        contactsDict.put(cd.username, cd);
                    }
                }catch (Exception e) {
                    cons = null;
                }
                ContactsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cons == null) {
                            Toast.makeText(ContactsActivity.this, "获取通讯录失败！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    //点击条目
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(ContactsActivity.this, contacts.get(position).username, Toast.LENGTH_SHORT).show();
            jumpToContactsProfile(contacts.get(position));
        }
    };
    //跳转到联系人简介
    private void jumpToContactsProfile(ContactData data) {
        Intent intent = new Intent(ContactsActivity.this, ContactProfileActivity.class);
        intent.putExtra("nickname", nickname);
        intent.putExtra("wxId", wxId);
        intent.putExtra("contactWxId", data.username);
        intent.putExtra("contactNickname", data.nickname);
        intent.putExtra("contactConRemark", data.conRemark);
        startActivity(intent);
    }
}