package com.example.fmodule.other;

import android.database.Cursor;

public class ContactData {

    public String username;
    public String nickname;
    public String alias;
    public String conRemark;

    public void convertFrom(Cursor cursor) {
        username = cursor.getString(0);
        nickname = cursor.getString(1);
        alias = cursor.getString(2);
        conRemark = cursor.getString(3);
    }
}
