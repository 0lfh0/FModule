package com.example.fmodule;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fmodule.message.useravatar.PUserAvatar;
import com.example.fmodule.message.useravatar.QUserAvatar;
import com.example.fmodule.other.NetHelper;

import easynet.network.Session;

public class ContactProfileActivity extends AppCompatActivity {

    private String wxId;
    private String nickname;
    private String contactWxId;
    private String contactNickname;
    private String contactConRemark;
    private Session session;
    private MService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_profile);
        setTitle("联系人");
        Intent intent = getIntent();
        wxId = intent.getStringExtra("wxId");
        nickname = intent.getStringExtra("nickname");
        contactWxId = intent.getStringExtra("contactWxId");
        contactNickname = intent.getStringExtra("contactNickname");
        contactConRemark = intent.getStringExtra("contactConRemark");

        TextView nicknameText = findViewById(R.id.nicknameText);
        TextView wxIdText = findViewById(R.id.wxIdText);
        nicknameText.setText(contactNickname);
        wxIdText.setText(contactWxId);
        Button replyMsgBtn = findViewById(R.id.replyMsgBtn);
        replyMsgBtn.setOnClickListener(onClickListener);
        findViewById(R.id.sendMsgBtn).setOnClickListener(onClickListener);

        Intent serviceIntent = new Intent(this, MService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
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
            loadAvatarAsync();
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
    //异步加载头像
    private void loadAvatarAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                QUserAvatar qUserAvatar = new QUserAvatar();
                qUserAvatar.wxId = contactWxId;
                PUserAvatar pUserAvatar = (PUserAvatar) session.call(qUserAvatar).execute();
                byte[] bytes = pUserAvatar.avatarData;
                if (bytes == null) {
                    pUserAvatar = (PUserAvatar) session.call(qUserAvatar).execute();
                    bytes = pUserAvatar.avatarData;
                    if (bytes == null) {
                        return;
                    }
                }
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ContactProfileActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView avatarImage = findViewById(R.id.avatarImage);
                        avatarImage.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();
    }
    //点击按钮
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.replyMsgBtn) {
                Intent intent = new Intent(ContactProfileActivity.this, ReplyActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("wxId", wxId);
                intent.putExtra("contactWxId", contactWxId);
                intent.putExtra("contactNickname", contactNickname);
                intent.putExtra("contactConRemark", contactConRemark);
                startActivity(intent);
            } else if (v.getId() == R.id.sendMsgBtn) {
                Intent intent = new Intent(ContactProfileActivity.this, SendActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("wxId", wxId);
                intent.putExtra("contactWxId", contactWxId);
                intent.putExtra("contactNickname", contactNickname);
                intent.putExtra("contactConRemark", contactConRemark);
                startActivity(intent);
            }
        }
    };
}