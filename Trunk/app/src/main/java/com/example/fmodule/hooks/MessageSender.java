package com.example.fmodule.hooks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MessageSender {

    private Class<?> eClass;
    private Class<?> dClass;
    private Method aHDMethod;
    private Method executeMethod;
    //消息类型：普通消息、图片、语音
    private Object[] msgTypes;
    private Object msgObj;

    private Field content;
    private Field dnZ;
    private Field dpB;
    private Field dpg;
    private Field dqW;
    private Field dtX;
    private Field ghY;
    private Field gif;
    private Field hMj;
    private Field hMs;
    private Field hOn;
    private Field hOp;
    private Field hSL;
    private Field hSM;
    private Field hSN;
    private Field hSO;
    private Field hSP;
    private Field hSQ;
    private Field hSR;
    private Field hSS;
    private Field hST;
    private Field hSU;
    private Field hSV;
    private Field hSW;
    private Field hSX;
    private Field msgId;
    private Field thumbPath;
    private Field toUser;
    private Field type;

    public MessageSender(XC_LoadPackage.LoadPackageParam lParam) {
        try {
            String str1 = "K636F6D2E74656E63656E742E6D6D2E6D6F64656C6D756C74692E6F2465L"; //com.tencent.mm.modelmulti.o$e
            eClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);

            String str2 = "C636F6D2E74656E63656E742E6D6D2E6D6F64656C6D756C74692E6F2464H"; //com.tencent.mm.modelmulti.o$d
            dClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str2), lParam.classLoader);
            aHDMethod = eClass.getMethod("aHD");
            msgTypes = (Object[]) dClass.getDeclaredMethod("values").invoke(null);

            content = eClass.getField("content");
            dnZ = eClass.getField("dnZ");
            dpB = eClass.getField("dpB");
            dpg = eClass.getField("dpg");
            dqW = eClass.getField("dqW");
            dtX = eClass.getField("dtX");
            ghY = eClass.getField("ghY");
            gif = eClass.getField("gif");
            hMj = eClass.getField("hMj");
            hMs = eClass.getField("hMs");
            hOn = eClass.getField("hOn");
            hOp = eClass.getField("hOp");
            hSL = eClass.getField("hSL");
            hSM = eClass.getField("hSM");
            hSN = eClass.getField("hSN");
            hSO = eClass.getField("hSO");
            hSP = eClass.getField("hSP");
            hSQ = eClass.getField("hSQ");
            hSR = eClass.getField("hSR");
            hSS = eClass.getField("hSS");
            hST = eClass.getField("hST");
            hSU = eClass.getField("hSU");
            hSV = eClass.getField("hSV");
            hSW = eClass.getField("hSW");
            hSX = eClass.getField("hSX");
            msgId = eClass.getField("msgId");
            thumbPath = eClass.getField("thumbPath");
            toUser = eClass.getField("toUser");
            type = eClass.getField("type");
        }catch (Exception e) {
            XposedBridge.log("initialize MessageSender error： " + e);
        }
    }

    private Object createMsgObj(String toUserId, String msg) throws Exception{
        Object obj = eClass.newInstance();
        content.set(obj, msg);
        dnZ.setInt(obj, 0);
        dpB.set(obj, null);
        dpg.setFloat(obj, 0.0f);
        dqW.setFloat(obj, 0.0f);
        dtX.setInt(obj, 0);
        ghY.setInt(obj, 0);
        gif.setBoolean(obj, false);
        hMj.setInt(obj, 0);
        hMs.set(obj, "");
        hOn.set(obj, null);
        hOp.set(obj, "");
        hSL.set(obj, null);
        hSM.setInt(obj, 5);
        hSN.set(obj, null);
        hSO.set(obj, null);
        hSP.setInt(obj, 0);
        hSQ.setBoolean(obj, false);
        hSR.setLong(obj, 0);
        hSS.setLong(obj, 0);
        hST.set(obj, null);
        hSU.setInt(obj, 0);
        hSV.setBoolean(obj, false);
        hSW.setInt(obj, 0);
        //消息类型：普通消息、图片、语音
        hSX.set(obj, msgTypes[0]);
        msgId.setLong(obj, 0);
        toUser.set(obj, toUserId);
        type.setInt(obj, 1);
        return obj;
    }

    public void send(String toUserId, String msg) throws Exception {
        if (toUserId == null || toUserId.isEmpty() || msg == null) {
            return;
        }
        if (msgObj == null) {
            msgObj = createMsgObj(toUserId, msg);
        }
        content.set(msgObj, msg);
        toUser.set(msgObj, toUserId);
        Object o1 = aHDMethod.invoke(msgObj);
        if (executeMethod == null) {
            executeMethod = o1.getClass().getMethod("execute");
        }
        executeMethod.invoke(o1);
    }
}
