package com.example.fmodule;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.fmodule.eventsystem.EventIdType;
import com.example.fmodule.eventsystem.EventSystem;
import com.example.fmodule.hooktask.DiceTask;
import com.example.fmodule.hooktask.HookTaskHelper;
import com.example.fmodule.hooktask.ReplyTask;
import com.example.fmodule.hooktask.SendTask;
import com.example.fmodule.hooktask.VoiceLenTask;
import com.example.fmodule.message.MessageManifest;
import com.example.fmodule.message.hooktask.MDiceTask;
import com.example.fmodule.message.hooktask.MReplyTask;
import com.example.fmodule.message.hooktask.MSendTask;
import com.example.fmodule.message.hooktask.MVoiceLenTask;
import com.example.fmodule.message.identify.PIdentify;
import com.example.fmodule.message.identify.QIdentify;
import com.example.fmodule.other.NetHelper;
import com.example.fmodule.sqlite.SQLiteHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import easynet.network.MessageHandler;
import easynet.network.Network;
import easynet.network.Session;

public class MService extends Service {
    private final static String TAG = "MService";
    private LocalBinder binder = new LocalBinder();
    private Network network;
    private final Hashtable<String, Session> wxSessions = new Hashtable<>();
    private final Hashtable<Session, Map<String, Object>> sessionInfo = new Hashtable<>();
    private final ArrayList<Map<String, Object>> infoList = new ArrayList<>();
    private SQLiteHelper mSQLiteHelper;
    private final HashMap<Integer, Timer> sendTaskTimers = new HashMap<>();
    private Handler mHandler = new Handler();

    /**
     * 创建Binder对象，返回给客户端即Activity使用，提供数据交换的接口
     */
    public class LocalBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        MService getService() {
            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
            return MService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service is invoke Created");
        mSQLiteHelper = new SQLiteHelper(this, "hookTask_db", null, 1);
        //启动服务
        network = new Network("0.0.0.0", 63333);
        network.setMessageMgr(new MessageManifest());
        network.start();
        network.getMessageDispatcher().registerHandlers(this);
        Toast.makeText(this, "FModule服务已启动", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service is invoke onUnbind");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        Log.i(TAG, "Service is invoke Destroyed");
        mSQLiteHelper.close();
        network.getMessageDispatcher().unregisterHandlers(this);
        super.onDestroy();
    }
    //身份验证
    @MessageHandler
    public void qIdentifyHandler(Session session, QIdentify qIdentify) {
        if (qIdentify.identity == QIdentify.wx) {
            Log.d(TAG, "qIdentifyHandler: "+ session.getRemoteAddress().getHostAddress() + ":" + session.getRemotePort());
            PIdentify pIdentify = new PIdentify();
            pIdentify.rpcId = qIdentify.rpcId;
            if (qIdentify.id == null || qIdentify.id.trim().equals("") || wxSessions.containsKey(qIdentify.id)) {
                pIdentify.result = false;
                session.send(pIdentify);
                return;
            }
            pIdentify.result = true;
            session.send(pIdentify);

            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
            //启动全部的发送任务
            startSendTasks(db, qIdentify.id);
            //同步全部的自动回复任务
            syncReplyTasks(db, session, qIdentify.id);
            syncDiceTask(db, session, qIdentify.id);
            syncVoiceLenTask(db, session, qIdentify.id);

            Map<String, Object> map = new HashMap<>();
            map.put("wxId", qIdentify.id);
            map.put("nickname", qIdentify.nickname);
            map.put("avatarBase64", qIdentify.avatarBase64);
            map.put("session", session);
            updateSessionInfo(session, qIdentify.id, map);
        }
    }
    //启动发送消息任务
    private void startSendTasks(SQLiteDatabase db, String wxId) {
        Cursor cursor = db.query(SQLiteHelper.tableSendTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        SendTask sendTask = new SendTask();
        Calendar now = Calendar.getInstance();
        while (cursor.moveToNext()) {
            sendTask.convertFrom(cursor);
            boolean isCreated = createSendTaskTimer(sendTask, now);
            if (!isCreated) {
                db.delete(SQLiteHelper.tableSendTask, "id=?", new String[]{String.valueOf(sendTask.id)});
            } else {
                sendTask = new SendTask();
            }
        }
        cursor.close();
    }
    //同步自动回复任务
    private void syncReplyTasks(SQLiteDatabase db, Session session, String wxId) {
        Cursor cursor = db.query(SQLiteHelper.tableReplyTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        MReplyTask mReplyTask = null;
        while (cursor.moveToNext()) {
            if (mReplyTask == null) {
                mReplyTask = new MReplyTask();
                mReplyTask.opType = MReplyTask.OpType.add;
                mReplyTask.replyTask = new ReplyTask();
            }
            mReplyTask.replyTask.convertFrom(cursor);
            session.send(mReplyTask);
        }
        cursor.close();
    }
    //同步骰子任务
    private void syncDiceTask(SQLiteDatabase db, Session session, String wxId) {
        Cursor cursor = db.query(SQLiteHelper.tableDiceTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        if (cursor.moveToNext()) {
            DiceTask task = new DiceTask();
            task.convertFrom(cursor);
            MDiceTask message = new MDiceTask();
            message.diceTask = task;
            session.send(message);
        }
        cursor.close();
    }
    //同步语音秒数任务
    private void syncVoiceLenTask(SQLiteDatabase db, Session session, String wxId) {
        Cursor cursor = db.query(SQLiteHelper.tableVoiceLenTask, null, "wxId=?", new String[]{wxId}, null, null, null);
        if (cursor.moveToNext()) {
            VoiceLenTask task = new VoiceLenTask();
            task.convertFrom(cursor);
            MVoiceLenTask message = new MVoiceLenTask();
            message.task = task;
            session.send(message);
        }
        cursor.close();
    }
    //保存会话信息
    private synchronized void updateSessionInfo(Session session, String wxId, Map<String, Object> data) {
        wxSessions.put(wxId, session);
        sessionInfo.put(session, data);
        infoList.add(data);
        session.registerErrorCallback(new Session.ErrorCallback() {
            @Override
            public void run(Session session, Exception e) {
                wxSessions.remove(wxId);
                sessionInfo.remove(session);
                infoList.remove(data);
                EventSystem.getInstance().run(EventIdType.mServiceSessionRemoved, wxId);
                EventSystem.getInstance().run(EventIdType.mServiceSessionChanged, wxId);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MService.this, "FModule与微信断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        EventSystem.getInstance().run(EventIdType.mServiceSessionAdded, wxId);
        EventSystem.getInstance().run(EventIdType.mServiceSessionChanged, wxId);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MService.this, "FModule与微信建立连接", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //----------------Utility---------------
    //Network
    public Network getNetwork() {
        return network;
    }
    //infoList
    public ArrayList<Map<String, Object>> getInfoList() {
        return infoList;
    }
    //wx session
    public Session getWxSession(String wxId) {
        return wxSessions.get(wxId);
    }
    //获取数据库helper
    public SQLiteHelper getSQLiteHelper() {
        return mSQLiteHelper;
    }
    //创建发送任务的计时器
    public boolean createSendTaskTimer(SendTask task, Calendar moment) {
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.setTime(task.sendDate);
        if (taskCalendar.before(moment)) {
            return false;
        }
        Timer timer = sendTaskTimers.remove(task.id);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MSendTask message = new MSendTask();
                message.sendTask = task;
                wxSessions.get(task.wxId).send(message);
                SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
                db.delete(SQLiteHelper.tableSendTask, "id=?", new String[] {String.valueOf(task.id)});
                sendTaskTimers.remove(task.id);
                EventSystem.getInstance().run(EventIdType.sendTaskCompleted, task.id);
            }
        }, taskCalendar.getTime());

        sendTaskTimers.put(task.id, timer);
        return true;
    }
    //创建发送任务的计时器
    public boolean createSendTaskTimer(SendTask task) {
        return createSendTaskTimer(task, Calendar.getInstance());
    }
    //取消发送任务的计时器
    public void cancelSendTaskTimer(int id) {
        Timer timer = sendTaskTimers.remove(id);
        if (timer != null) {
            timer.cancel();
        }
    }
}
