package com.example.fmodule.hooktask;

import android.database.Cursor;

public class VoiceLenTask {
    public String wxId;
    public boolean isOn;
    public int number;

    public VoiceLenTask() {
        isOn = false;
        number = 1;
    }

    public void convertFrom(Cursor cursor) {
        wxId = cursor.getString(0);
        isOn = cursor.getInt(1) == 1;
        number = cursor.getInt(2);
    }
}
