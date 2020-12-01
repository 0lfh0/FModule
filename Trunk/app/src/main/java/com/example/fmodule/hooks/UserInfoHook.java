package com.example.fmodule.hooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.fmodule.message.MessageManifest;
import com.example.fmodule.message.identify.PIdentify;
import com.example.fmodule.message.identify.QIdentify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import easynet.common.IAction1;
import easynet.network.Network;
import easynet.network.Session;

public class UserInfoHook implements IXposedHookLoadPackage {
    private Context wxContext;
    private String wxId;
    private String nickname;
    private String avatarBase64;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            XposedBridge.log("开始劫持");
            hookContext(loadPackageParam);
            //hook4(loadPackageParam);
            getUserInfo(loadPackageParam);
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

    private void hook3(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.model.u", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayv",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayv, result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayw, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                clazz,
                "getUserBindEmail",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了getUserBindEmail, result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了getUserBindEmail, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //微信号
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayw",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayw , result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayw, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                        param.setResult("Can1016457460");
                    }
                }
        );

        XposedHelpers.findAndHookMethod(
                clazz,
                "ayx",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayx, result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayx, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //微信昵称
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayy",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayy result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayy, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        //微信号
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayz",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayz result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayz, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayA",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayA result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayA, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                        //param.setResult(137510);
                    }
                }
        );
        XposedHelpers.findAndHookMethod(
                clazz,
                "ayE",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了ayE result = " + param.getResult());
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了ayE, result = " + param.getResult(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
//        XposedHelpers.findAndHookMethod(
//                clazz,
//                "azp",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        XposedBridge.log("调用了azp ---------------");
//                        if (wxContext != null) {
//                            Toast.makeText(wxContext, "调用了azp", Toast.LENGTH_SHORT).show();
//                        }
//                        Map<String, String> map = (Map<String, String>)param.getResult();
//                        for(String key : map.keySet()) {
//                            XposedBridge.log(key + " : " + map.get(key));
//                        }
//                        XposedBridge.log("================");
//                    }
//                }
//        );
//        try {
//            Method m = clazz.getMethod("ayw");
//            String str = (String)m.invoke(null);
//            XposedBridge.log("调用完成，str = " + str);
//        }catch (Exception e) {
//            XposedBridge.log(e);
//        }
    }

    //hook头像
    private void hook4(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> cClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.c", lParam.classLoader);
        Class<?> jClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.j", lParam.classLoader);
        Class<?> j_aClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.j$a", lParam.classLoader);
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.a$b", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "c",
                ImageView.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用了a.b.c: " + "str=" + param.args[1]);
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "调用了a.b.c", Toast.LENGTH_SHORT).show();
                        }

                        try {
                            ImageView iv = (ImageView) param.args[0];
//                            Object cObj = cClass.getConstructor(String.class, float.class).newInstance("filehelper", 0.1f);
//                            cClass.getMethod("vm", boolean.class).invoke(cObj, false);
//                            BitmapDrawable bd = (BitmapDrawable)cObj;
//                            Bitmap bitmap = bd.getBitmap();
                            //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            //byte[] bytes = stream.toByteArray();
                            //avatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                            //byte[] avatarBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                            //Bitmap bm = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                            //Drawable avatarDrawable = new BitmapDrawable(bitmap);
                            //iv.setImageDrawable(avatarDrawable);
                            //iv.setImageBitmap(bitmap);

                            //方案2
//                            Object j_aObj = clazz.getMethod("eTU").invoke(null);
//                            Bitmap bm = (Bitmap)j_aClass.getMethod("ej", String.class).invoke(j_aObj, "filehelper");
//                            iv.setImageBitmap(bm);

                            //方案3
                            Object cObj = cClass.getConstructor(String.class, float.class).newInstance("filehelper", 0.1f);
                            Field f = jClass.getDeclaredField("wxV");
                            f.setAccessible(true);
                            Object wxVObj = f.get(cObj);
                            Bitmap bm = (Bitmap)j_aClass.getMethod("ej", String.class).invoke(wxVObj, "filehelper");
                            XposedBridge.log("bm == null: " + (bm == null));
                            iv.setImageBitmap(bm);
                            File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(file);
                                bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                out.flush();
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(wxContext,"保存已经至"+Environment.getExternalStorageDirectory()+"下", Toast.LENGTH_SHORT).show();

                        }catch (Exception e) {
                            XposedBridge.log(e);
                            return;
                        }
                    }
                }
        );
    }

    private void getUserInfo(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> uClass = XposedHelpers.findClass("com.tencent.mm.model.u", lParam.classLoader);
        Class<?> cClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.c", lParam.classLoader);
        Class<?> jClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.j", lParam.classLoader);
        Class<?> j_aClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.j$a", lParam.classLoader);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        XposedBridge.log("第" + (++i) + "次尝试：");
                        //拿微信号
                        wxId = (String)uClass.getMethod("ayz").invoke(null);
                        XposedBridge.log("成功拿到了微信号：" + wxId);
                        //拿微信昵称
                        nickname = (String)uClass.getMethod("ayy").invoke(null);
                        XposedBridge.log("成功拿到了微信昵称：" + nickname);
                        //拿微信头像
                        Object cObj = cClass.getConstructor(String.class, float.class).newInstance(wxId, 0.1f);
                        cClass.getMethod("vm", boolean.class).invoke(cObj, false);
                        Field f = jClass.getDeclaredField("wxV");
                        f.setAccessible(true);
                        Object wxVObj = f.get(cObj);
                        //Kz是默认头像
                        Bitmap bitmap = (Bitmap)j_aClass.getMethod("ej", String.class).invoke(wxVObj, wxId);
                        XposedBridge.log("bitmap == null   " + (bitmap == null));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bytes = stream.toByteArray();
                        avatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                        XposedBridge.log("成功拿到了微信头像：" + avatarBase64);
                        //获取last数据（比如最后登录的手机号）
                        Map<String, String> map = (Map<String, String>)uClass.getMethod("azp").invoke(null);
                        XposedBridge.log("成功拿到map数据：");
                        for(String key : map.keySet()) {
                            XposedBridge.log(key + " : " + map.get(key));
                        }
                        XposedBridge.log("================");
                        //connectServer();
                        return;
                    }catch (Exception e) {
                        if (i >= 5) {
                            return;
                        }
                        try {
                            Thread.sleep(3000);
                        }catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    //连接服务器
    private void connectServer() {
        Network network = new Network();
        network.setMessageMgr(new MessageManifest());
        network.start();
    }
    //连接成功
    private IAction1<Session> connectHandler = new IAction1<Session>() {
        @Override
        public void invoke(Session session) throws Exception {
            XposedBridge.log("连接成功，开始请求验证身份");
            QIdentify qIdentify = new QIdentify();
            qIdentify.identity = QIdentify.wx;
            qIdentify.id = wxId;
            qIdentify.nickname = nickname;
            qIdentify.avatarBase64 = avatarBase64;
            String json = JSON.toJSONString(qIdentify);
            byte[] bytes = json.getBytes("UTF-8");
            XposedBridge.log("消息长度： " + bytes.length);
            PIdentify response = (PIdentify) session.call(qIdentify).execute();
            XposedBridge.log("身份验证结果： " + response.result);
            if (!response.result) {
                session.dispose();
            }
        }
    };
}
