package com.example.fmodule.hooktask;

import android.database.Cursor;

import java.util.Date;

public class SendTask {
    public int id;
    public String wxId;
    public String toUser;
    public String toUsers;
    public enum SendMode { fixedText, emojiUrl, serverApi }
    public SendMode sendMode;
    public String sendMsg;
    public String emojiUrl;
    public String serverApi;
    public enum SendTimeMode { timeout_5s, timeout_30s, timeout_1m, custom }
    public SendTimeMode sendTimeMode;
    public Date sendDate;

    public SendTask() {
        id = -1;
        sendMode = SendMode.fixedText;
        sendTimeMode = SendTimeMode.timeout_5s;
    }

    public void convertFrom(Cursor cursor) {
        id = cursor.getInt(0);
        wxId = cursor.getString(1);
        toUser = cursor.getString(2);
        toUsers = cursor.getString(3);
        sendMode = SendMode.valueOf(cursor.getString(4));
        sendMsg = cursor.getString(5);
        emojiUrl = cursor.getString(6);
        serverApi = cursor.getString(7);
        sendTimeMode = SendTimeMode.valueOf(cursor.getString(8));
        sendDate = new Date();
        sendDate.setTime(cursor.getLong(9));
    }
}
