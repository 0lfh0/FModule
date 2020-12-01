package com.example.fmodule.hooktask;

import android.database.Cursor;

public class ReplyTask {
    public int id;
    public String wxId;
    public String fromUser;
    public boolean isOn;
    public enum PatternMode { fuzzy, precise, all }
    public PatternMode patternMode;
    public String patternMsg;
    public enum ReplyMode { fixedText, emojiUrl, serverApi }
    public ReplyMode replyMode;
    public String replyMsg;
    public String emojiUrl;
    public String serverApi;

    public ReplyTask() {
        id = -1;
        isOn = false;
        patternMode = PatternMode.fuzzy;
        replyMode = ReplyMode.fixedText;
    }

    public void convertFrom(Cursor cursor) {
        id = cursor.getInt(0);
        wxId = cursor.getString(1);
        fromUser = cursor.getString(2);
        isOn = cursor.getInt(3) == 1;
        patternMode = PatternMode.valueOf(cursor.getString(4));
        patternMsg = cursor.getString(5);
        replyMode = ReplyMode.valueOf(cursor.getString(6));
        replyMsg = cursor.getString(7);
        emojiUrl = cursor.getString(8);
        serverApi = cursor.getString(9);
    }
}
