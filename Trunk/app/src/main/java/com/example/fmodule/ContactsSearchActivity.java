package com.example.fmodule;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.message.contacts.PContacts;
import com.example.fmodule.message.contacts.QContacts;
import com.example.fmodule.other.ContactData;
import com.example.fmodule.other.NetHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import easynet.network.Session;

public class ContactsSearchActivity extends AppCompatActivity {

    private String wxId;
    private boolean multiple;
    private ArrayList<ContactData> contacts;
    private final ArrayList<ContactData> listData = new ArrayList<>();
    private LinkedList<ContactData> selectedList;
    private LinkedList<ContactData> noneSelectedList;

    private ContactsSearchListAdapter adapter;
    private HashSet<String> alreadySelected;
    private Session session;

    private MService mService;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_search);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        multiple = intent.getBooleanExtra("multiple", false);
        if (multiple) {
            alreadySelected = new HashSet<>();
            int alreadySelectedCount = intent.getIntExtra("selectedCount", 0);
            for (int i=0; i<alreadySelectedCount; i++) {
                alreadySelected.add(intent.getStringExtra("selectedItem-" + i));
            }
        }
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(multiple ? null : onItemClickListener);

        EditText searchBarEdit = findViewById(R.id.searchBar);
        searchBarEdit.addTextChangedListener(textWatcher);

        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent();
            if (multiple) {
                intent.putExtra("selectedCount", selectedList.size());
                int i = 0;
                for(ContactData data : selectedList) {
                    intent.putExtra("selectedItem-" + i, data.username);
                    i++;
                }
            } else {
                intent.putExtra("selectedCount", 0);
            }
            setResult(1, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        session.unregisterErrorCallback(errorCallback);
        unbindService(conn);
        super.onDestroy();
    }
    //初始化页面
    private void initializeUi() {
        if (multiple) {
            adapter = new ContactsSearchListAdapter(ContactsSearchActivity.this, listData, selectedList, noneSelectedList);
        } else {
            adapter = new ContactsSearchListAdapter(ContactsSearchActivity.this, listData);
        }
        listView.setAdapter(adapter);
    }
    //绑定服务回调
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MService.LocalBinder binder = (MService.LocalBinder)service;
            mService = binder.getService();
            session = mService.getWxSession(wxId);
            session.registerErrorCallback(errorCallback);
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
                    contacts = cons.contacts;
                    if (multiple) {
                        selectedList = new LinkedList<>();
                        noneSelectedList = new LinkedList<>();
                        for(ContactData data : cons.contacts) {
                            if (alreadySelected.contains(data.username)) {
                                selectedList.offer(data);
                            } else {
                                noneSelectedList.offer(data);
                            }
                        }
                        listData.addAll(selectedList);
                        listData.addAll(noneSelectedList);
                    } else {
                        listData.addAll(contacts);
                    }
                }catch (Exception e) {
                    cons = null;
                }

                ContactsSearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cons == null) {
                            Toast.makeText(ContactsSearchActivity.this, "获取通讯录失败！", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        initializeUi();
                    }
                });
            }
        }).start();
    }
    //监听搜索文本
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (contacts == null) {
                //Toast.makeText(ContactsSearchActivity.this, "网络请求失败！", Toast.LENGTH_SHORT).show();
                return;
            }
            String word = s.toString().trim();
            listData.clear();
            if (!word.isEmpty()) {
                for (ContactData cd : contacts) {
                    if (cd.conRemark!=null && cd.conRemark.contains(word)) {
                        listData.add(cd);
                    }else if(cd.nickname != null && cd.nickname.contains(word)){
                        listData.add(cd);
                    }else if (cd.username != null && cd.username.contains(word)) {
                        listData.add(cd);
                    }
                }
            } else if (multiple){
                listData.addAll(selectedList);
                listData.addAll(noneSelectedList);
            } else {
                listData.addAll(contacts);
            }
            adapter.notifyDataSetChanged();
        }
    };
    //点击搜索结果条目
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent();
            intent.putExtra("selectedCount", 1);
            String username = listData.get(position).username;
            intent.putExtra("selectedItem-0", username);
            setResult(1, intent);
            finish();
        }
    };
}