package com.example.fmodule.hooktask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.example.fmodule.MainActivity;
import com.example.fmodule.eventsystem.EventIdType;
import com.example.fmodule.eventsystem.EventSystem;
import com.example.fmodule.message.hooktask.MSendTask;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import easynet.network.CircularBuffer;

import static android.content.Context.MODE_PRIVATE;

public class HookTaskHelper {
    public static void saveHookTask(Context context, HookTask hookTask) {
        try {
            String dataStr = JSON.toJSONString(hookTask);
            byte[] bytes = dataStr.getBytes("UTF-8");
            FileOutputStream stream = context.openFileOutput(hookTask.wxId + ".txt", MODE_PRIVATE);
            stream.write(bytes);
            stream.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CircularBuffer readBuffer;
    public static HookTask getHookTask(Context context, String wxId) {
        if (readBuffer == null) {
            readBuffer = new CircularBuffer();
        }
        readBuffer.flush();
        try {
            FileInputStream stream = context.openFileInput(wxId + ".txt");
            byte[] bytes = new byte[1024];
            while(true) {
                byte[] buffer = readBuffer.last();
                int offset = readBuffer.getLastIndex();
                int count = readBuffer.getChunkSize() - readBuffer.getLastIndex();
                int len = stream.read(buffer, offset, count);
                if (len == -1) {
                    break;
                }else {
                    readBuffer.setLastIndexByOffset(len);
                }
            }
            byte[] data = readBuffer.toByteArray();
            String dataStr = new String(data, "UTF-8");
            HookTask hookTask = JSON.parseObject(dataStr, HookTask.class);
            return hookTask;
        }catch (Exception e) {
            e.printStackTrace();
            HookTask ht = new HookTask();
            ht.wxId = wxId;
            return ht;
        }
    }

}
