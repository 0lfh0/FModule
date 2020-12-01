package com.example.fmodule.hooks;

import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.Vector;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SendMessageHook implements IXposedHookLoadPackage {
    private XC_LoadPackage.LoadPackageParam lParam;
    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            lParam = loadPackageParam;
            XposedBridge.log("开始劫持");
            hookContext(loadPackageParam);
            hook2();
        }
    }

    private void hookContext(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("android.content.ContextWrapper", lParam.classLoader);

        XposedHelpers.findAndHookMethod(
                clazz,
                "getApplicationContext",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if (wxContext != null) {
                            return;
                        }
                        wxContext = (Context) param.getResult();
                    }
                }
        );
    }

    private void hook0(XC_LoadPackage.LoadPackageParam lParam){
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.chatting.p", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "amk",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("劫持到p.amk之前： str = " + param.args[0]);
                        param.args[0] = "hello world";
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                }
        );
    }

    private void hook1(XC_LoadPackage.LoadPackageParam lParam){
        final Class<?> oVarClass = XposedHelpers.findClass("com.tencent.mm.am.o", lParam.classLoader);
        final Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.am.r", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "b",
                oVarClass,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("劫持到am.r.b之前：");
//                        Method[] ms = clazz.getMethods();
//                        for(Method me : ms) {
//                            XposedBridge.log("method: " + me.getName() + me.getParameterTypes());
//                        }
                        //Method m = XposedHelpers.findMethodBestMatch(clazz, "b", param.args[0], param.args[1]);
                        //m.invoke(param.thisObject, param.args[0], param.args[1]);

                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("劫持到am.r.b之后：");
                        for(Field fi : clazz.getFields()) {
                            XposedBridge.log("Field: " + fi.getName() );
                        }
                        XposedBridge.log("---------------");
                        Field f = clazz.getDeclaredField("hBx");
                        f.setAccessible(true);
                        //Vector<Object> os = (Vector<Object>) f.get(param.thisObject);
                        //os.clear();
                        Vector.class.getMethod("clear").invoke(f.get(param.thisObject));
                    }
                }
        );
    }

    //每次切回到微信页面，都会发送一条空消息
    private void hook2() throws Throwable{
        final Class<?> oVarClass = XposedHelpers.findClass("com.tencent.mm.am.o", lParam.classLoader);
        final Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.modelmulti.o$e", lParam.classLoader);
        final Class<?> dClass = XposedHelpers.findClass("com.tencent.mm.modelmulti.o$d", lParam.classLoader);
        final Field content = clazz.getField("content");
        final Field dnZ = clazz.getField("dnZ");
        final Field dpB = clazz.getField("dpB");
        final Field dpg = clazz.getField("dpg");
        final Field dqW = clazz.getField("dqW");
        final Field dtX = clazz.getField("dtX");
        final Field ghY = clazz.getField("ghY");
        final Field gif = clazz.getField("gif");
        final Field hMj = clazz.getField("hMj");
        final Field hMs = clazz.getField("hMs");
        final Field hOn = clazz.getField("hOn");
        final Field hOp = clazz.getField("hOp");
        final Field hSL = clazz.getField("hSL");
        final Field hSM = clazz.getField("hSM");
        final Field hSN = clazz.getField("hSN");
        final Field hSO = clazz.getField("hSO");
        final Field hSP = clazz.getField("hSP");
        final Field hSQ = clazz.getField("hSQ");
        final Field hSR = clazz.getField("hSR");
        final Field hSS = clazz.getField("hSS");
        final Field hST = clazz.getField("hST");
        final Field hSU = clazz.getField("hSU");
        final Field hSV = clazz.getField("hSV");
        final Field hSW = clazz.getField("hSW");
        final Field hSX = clazz.getField("hSX");
        final Field msgId = clazz.getField("msgId");
        final Field thumbPath = clazz.getField("thumbPath");
        final Field toUser = clazz.getField("toUser");
        final Field type = clazz.getField("type");

        XposedHelpers.findAndHookMethod(
                clazz,
                "aHD",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "劫持到aHD", Toast.LENGTH_SHORT).show();
                        }

                        XposedBridge.log("劫持到aHD之前：-----------------");
                        XposedBridge.log("content: " + content.get(param.thisObject));
                        XposedBridge.log("dnZ: " + dnZ.getInt(param.thisObject));
                        XposedBridge.log("dpB: " + dpB.get(param.thisObject));
                        XposedBridge.log("dpg: " + dpg.getFloat(param.thisObject));
                        XposedBridge.log("dqW: " + dqW.getFloat(param.thisObject));
                        XposedBridge.log("dtX: " + dtX.getInt(param.thisObject));
                        XposedBridge.log("ghY: " + ghY.getInt(param.thisObject));
                        XposedBridge.log("gif: " + gif.getBoolean(param.thisObject));
                        XposedBridge.log("hMj: " + hMj.getInt(param.thisObject));
                        XposedBridge.log("hMs: " + hMs.get(param.thisObject));
                        XposedBridge.log("hOn: " + hOn.get(param.thisObject));
                        XposedBridge.log("hOp: " + hOp.get(param.thisObject));
                        XposedBridge.log("hSL: " + hSL.get(param.thisObject));
                        XposedBridge.log("hSM: " + hSM.getInt(param.thisObject));
                        XposedBridge.log("hSN: " + hSN.get(param.thisObject));
                        XposedBridge.log("hSO: " + hSO.get(param.thisObject));
                        XposedBridge.log("hSP: " + hSP.getInt(param.thisObject));
                        XposedBridge.log("hSQ: " + hSQ.getBoolean(param.thisObject));
                        XposedBridge.log("hSR: " + hSR.getLong(param.thisObject));
                        XposedBridge.log("hSS: " + hSS.getLong(param.thisObject));
                        XposedBridge.log("hST: " + hST.get(param.thisObject));
                        XposedBridge.log("hSU: " + hSU.getInt(param.thisObject));
                        XposedBridge.log("hSV: " + hSV.getBoolean(param.thisObject));
                        XposedBridge.log("hSW: " + hSW.getInt(param.thisObject));
                        XposedBridge.log("hSX: " + hSX.get(param.thisObject));
                        XposedBridge.log("msgId: " + msgId.getLong(param.thisObject));
                        XposedBridge.log("thumbPath: " + thumbPath.get(param.thisObject));
                        XposedBridge.log("toUser: " + toUser.get(param.thisObject));
                        XposedBridge.log("type: " + type.getInt(param.thisObject));
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("劫持到am.r.b之后：-----------------");
                        XposedBridge.log("content: " + content.get(param.thisObject));
                        XposedBridge.log("dnZ: " + dnZ.getInt(param.thisObject));
                        XposedBridge.log("dpB: " + dpB.get(param.thisObject));
                        XposedBridge.log("dpg: " + dpg.getFloat(param.thisObject));
                        XposedBridge.log("dqW: " + dqW.getFloat(param.thisObject));
                        XposedBridge.log("dtX: " + dtX.getInt(param.thisObject));
                        XposedBridge.log("ghY: " + ghY.getInt(param.thisObject));
                        XposedBridge.log("gif: " + gif.getBoolean(param.thisObject));
                        XposedBridge.log("hMj: " + hMj.getInt(param.thisObject));
                        XposedBridge.log("hMs: " + hMs.get(param.thisObject));
                        XposedBridge.log("hOn: " + hOn.get(param.thisObject));
                        XposedBridge.log("hOp: " + hOp.get(param.thisObject));
                        XposedBridge.log("hSL: " + hSL.get(param.thisObject));
                        XposedBridge.log("hSM: " + hSM.getInt(param.thisObject));
                        XposedBridge.log("hSN: " + hSN.get(param.thisObject));
                        XposedBridge.log("hSO: " + hSO.get(param.thisObject));
                        XposedBridge.log("hSP: " + hSP.getInt(param.thisObject));
                        XposedBridge.log("hSQ: " + hSQ.getBoolean(param.thisObject));
                        XposedBridge.log("hSR: " + hSR.getLong(param.thisObject));
                        XposedBridge.log("hSS: " + hSS.getLong(param.thisObject));
                        XposedBridge.log("hST: " + hST.get(param.thisObject));
                        XposedBridge.log("hSU: " + hSU.getInt(param.thisObject));
                        XposedBridge.log("hSV: " + hSV.getBoolean(param.thisObject));
                        XposedBridge.log("hSW: " + hSW.getInt(param.thisObject));
                        XposedBridge.log("hSX: " + hSX.get(param.thisObject));
                        XposedBridge.log("msgId: " + msgId.getLong(param.thisObject));
                        XposedBridge.log("thumbPath: " + thumbPath.get(param.thisObject));
                        XposedBridge.log("toUser: " + toUser.get(param.thisObject));
                        XposedBridge.log("type: " + type.getInt(param.thisObject));
                    }
                }
        );

//        final Object obj = clazz.newInstance();
//        content.set(obj, "我是一条自动发送的消息");
//        dnZ.setInt(obj, 0);
//        dpB.set(obj, null);
//        dpg.setFloat(obj, 0.0f);
//        dqW.setFloat(obj, 0.0f);
//        dtX.setInt(obj, 0);
//        ghY.setInt(obj, 0);
//        gif.setBoolean(obj, false);
//        hMj.setInt(obj, 0);
//        hMs.set(obj, "");
//        hOn.set(obj, null);
//        hOp.set(obj, "");
//        hSL.set(obj, null);
//        hSM.setInt(obj, 5);
//        hSN.set(obj, null);
//        hSO.set(obj, null);
//        hSP.setInt(obj, 0);
//        hSQ.setBoolean(obj, false);
//        hSR.setLong(obj, 0);
//        hSS.setLong(obj, 0);
//        hST.set(obj, null);
//        hSU.setInt(obj, 0);
//        hSV.setBoolean(obj, false);
//        hSW.setInt(obj, 0);
//        hSX.set(obj, ((Object[]) dClass.getDeclaredMethod("values").invoke(null))[0]);
//        msgId.setLong(obj, 0);
//        toUser.set(obj, "filehelper");
//        type.setInt(obj, 1);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try
//                {
//                    Thread.sleep(15000);
//                    XposedBridge.log("倒计时结束");
//                    Object o1 = clazz.getMethod("aHD").invoke(obj);
//                    o1.getClass().getMethod("execute").invoke(o1);
//                }
//                catch (Exception e) {
//                    XposedBridge.log("报错了： " + e.getStackTrace());
//                }
//            }
//        }).start();

    }

}
