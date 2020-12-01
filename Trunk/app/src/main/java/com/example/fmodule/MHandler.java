package com.example.fmodule;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class MHandler extends Handler {
    private final String TAG = "MHandler";
    private static MHandler instance;
    private WeakReference<Context> wc;
    private long currentThreadId;

    public MHandler(Context context) {
        this.wc = new WeakReference<>(context);
        currentThreadId = Thread.currentThread().getId();
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);

    }

    public void toast(final String content, final int duration) {
        final Context context = wc.get();
        if (context == null) {
            return;
        }
        if (isCurrentThread()) {
            Toast.makeText(context, content, duration).show();
            return;
        }
        this.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, content, duration).show();
            }
        });
    }

    private boolean isCurrentThread() {
        return currentThreadId == Thread.currentThread().getId();
    }
}
