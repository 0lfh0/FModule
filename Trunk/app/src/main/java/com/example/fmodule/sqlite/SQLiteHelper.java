package com.example.fmodule.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String tableSendTask = "sendTask";
    public static final String tableReplyTask = "replyTask";
    public static final String tableDiceTask = "diceTask";
    public static final String tableVoiceLenTask = "voiceLenTask";

    private final String createSendTaskTable = "create table sendTask( " +
            "id        integer     primary key autoincrement, " +
            "wxId      text        not null, " +
            "toUser    text        not null, " + //all：发送给全部人
            "toUsers   text        ," +
            "sendMode  varchar(20)        not null," + //fixedText, serverApi
            "sendMsg   text        , " +
            "emojiUrl    text       , " +
            "serverApi text        , " +
            "sendTimeMode varchar(20)     not null, " + //timeout_5s, timeout_30s, timeout_1m, custom
            "sendDate  integer     not null" +
            ")";

    private final String createReplyTaskTable = "create table replyTask(" +
            "id          integer    primary key autoincrement, " +
            "wxId        text       not null, " +
            "fromUser    text       not null, " + //all：匹配全部人
            "isOn        integer    not null, " +
            "patternMode varchar(20) not null, " + //fuzzy, precise, all
            "patternMsg  text       , " +
            "replyMode   varchar(20) not null, " + // fixedText, serverApi
            "replyMsg    text       , " +
            "emojiUrl    text       , " +
            "serverApi   text " +
            ")";

    private final String createDiceTaskTable = "create table diceTask(" +
            "wxId text primary key, " +
            "isOn integer not null, " +
            "number integer not null" +
            ")";

    private final String createVoiceLenTaskTable = "create table voiceLenTask(" +
            "wxId text primary key," +
            "isOn integer not null," +
            "number integer not null" +
            ")";

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSendTaskTable);
        db.execSQL(createReplyTaskTable);
        db.execSQL(createDiceTaskTable);
        db.execSQL(createVoiceLenTaskTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
