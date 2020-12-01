package com.example.fmodule.hooks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendEmojiHook implements IXposedHookLoadPackage {
    private XC_LoadPackage.LoadPackageParam lParam;
    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            lParam = loadPackageParam;
            XposedBridge.log("开始劫持");
            hookContext(loadPackageParam);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(15000);
                        hook15();
                        //EmojiSender emojiSender = new EmojiSender(lParam, wxContext);
                        //emojiSender.send("wxid_tit258gxuw1p22", "http://emoji.qpic.cn/wx_emoji/kotBn8b6nHuMtJlZoLyxoWBTEtSczL9BsoUaDD7grmib2VicEJzV4TD2BEp7knXMXk/");
                    }catch (Exception e) {
                        XposedBridge.log("发送表情失败：" + e);
                    }
                }
            }).start();
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

    private void hook1() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.emoji.panel.a.d", lParam.classLoader);
        Class<?> yClass = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.y", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                Context.class,
                int.class,
                yClass,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        int type = yClass.getField("type").getInt(param.args[2]);
                        XposedBridge.log("调用之前i= " + param.args[1] + "  type= " + type);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        int type = yClass.getField("type").getInt(param.args[2]);
                        XposedBridge.log("调用之后type= " + type);
                    }
                }
        );
    }

    private void hook2() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.storage.br", lParam.classLoader);
        Class<?> bqClass = XposedHelpers.findClass("com.tencent.mm.storage.bq", lParam.classLoader);
        Class<?> egClass = XposedHelpers.findClass("com.tencent.mm.g.c.eg", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "ap",
                bqClass,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("hook到ap：");
                        ContentValues values = (ContentValues) egClass.getMethod("convertTo").invoke(param.args[0]);
                        for(Map.Entry<String, Object> item : values.valueSet())
                        {
                            XposedBridge.log(item.getKey() + " : " + item.getValue().toString());
                        }
                        XposedBridge.log("");
                        XposedBridge.log("");
                        XposedBridge.log("");
                    }
                }
        );
    }

    private void hook3() {
        Class<?> emojiInfoClass = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                emojiInfoClass,
                "GO",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (wxContext != null) {
                            Toast.makeText(wxContext, "hook到GO", Toast.LENGTH_SHORT).show();
                        }
                        XposedBridge.log("调用GO之前，str=" + param.args[0]);
                    }
                }
        );
    }

    private void hook4() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.transmit.SelectConversationUI$38", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                boolean.class,
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("args[0]=" + param.args[0] + "  args[1]=" + param.args[1] + "  args[2]=" + param.args[2]);
                    }
                }
        );
    }

    private void hook5() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.tools.o", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "lk",
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("args[0]=" + param.args[0] + "  args[1]=" + param.args[1]);
                    }
                }
        );
    }

    private void hook6() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.transmit.SelectConversationUI", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "r",
                int.class,
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        Field msgTypeField = clazz.getDeclaredField("msgType");
                        msgTypeField.setAccessible(true);
                        int msgType = msgTypeField.getInt(param.thisObject);
                        XposedBridge.log("msgType=" + msgType);
                        //XposedBridge.log("args[0]=" + param.args[0] + "  args[1]=" + param.args[1] + "  args[2]=" + param.args[2]);
                    }
                }
        );
    }

    private void hook7() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.transmit.MsgRetransmitUI", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "onActivityResult",
                int.class,
                int.class,
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("args[0]=" + param.args[0] + "  args[1]=" + param.args[1]);
                        Intent intent = (Intent)param.args[2];
                        intent.putExtra("custom_send_text", "hello world");
                    }
                }
        );
    }

    private void hook8() {
        //这个也可以发送消息和表情，但是内容为表情时需要把表情地址换成md5值
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.sdk.b.a", lParam.classLoader);
        Class<?> bClass = XposedHelpers.findClass("com.tencent.mm.sdk.b.b", lParam.classLoader);
        Class<?> ssClass = XposedHelpers.findClass("com.tencent.mm.g.a.ss", lParam.classLoader);
        Class<?> ss_aClass = XposedHelpers.findClass("com.tencent.mm.g.a.ss$a", lParam.classLoader);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        Object ssObj = ssClass.newInstance();
                        Object dxVObj = ssClass.getField("dxV").get(ssObj);
                        ss_aClass.getField("content").set(dxVObj, "https://emoji.qpic.cn/wx_emoji/kotBn8b6nHuMtJlZoLyxoWBTEtSczL9BsoUaDD7grmib2VicEJzV4TD2BEp7knXMXk/");
                        ss_aClass.getField("dxW").set(dxVObj, "wxid_tit258gxuw1p22");
                        ss_aClass.getField("flags").set(dxVObj, 0);
                        ss_aClass.getField("type").set(dxVObj, 1);
                        Object HeXObj = clazz.getField("HeX").get(null);
                        clazz.getMethod("l", bClass).invoke(HeXObj, ssObj);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
//        XposedHelpers.findAndHookMethod(
//                clazz,
//                "l",
//                bClass,
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Object obj = param.args[0];
//                        if (obj.getClass() != ssClass) {
//                            return;
//                        }
//                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
//                        Field dxVField = ssClass.getField("dxV");
//                        Object dxVObj = dxVField.get(param.args[0]);
//                        String content = (String)ss_aClass.getField("content").get(dxVObj);
//                        String dxW = (String)ss_aClass.getField("dxW").get(dxVObj);
//                        int flags = ss_aClass.getField("flags").getInt(dxVObj);
//                        int type = ss_aClass.getField("type").getInt(dxVObj);
//                        XposedBridge.log("content: " + content);
//                        XposedBridge.log("dxW: " + dxW);
//                        XposedBridge.log("flags: " + flags);
//                        XposedBridge.log("type: " + type);
//                        XposedBridge.log("");
//                        XposedBridge.log("");
//                        XposedBridge.log("");
//                    }
//                }
//        );
    }

    private void hook9() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.applet.o$a", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "aJw",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("args[0]=" + param.args[0]);
                        ///storage/emulated/0/Download/u=2051651383,909700034&fm=26&gp=0.jpg
                    }
                }
        );
    }

    private void hook10() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    Class<?> emojiInfoClass = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
                    Class<?> gClass = XposedHelpers.findClass("com.tencent.mm.kernel.g", lParam.classLoader);
                    Class<?> bdClass = XposedHelpers.findClass("com.tencent.mm.plugin.emoji.b.d", lParam.classLoader);

                    Object obj = gClass.getMethod("ad", Object.class.getClass()).invoke(null, bdClass);
                    XposedBridge.log(obj.getClass().getName() + "  obj == null  " + (obj == null));

                    Object obj1 = bdClass.getMethod("getEmojiMgr").invoke(obj);
                    XposedBridge.log(obj1.getClass().getName() + "  obj1 == null  " + (obj1 == null));

                    Class<?> adClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.a.d", lParam.classLoader);
                    Object emojiInfoObj = adClass.getMethod("Yy", String.class).invoke(obj1, "https://emoji.qpic.cn/wx_emoji/kotBn8b6nHuMtJlZoLyxoWBTEtSczL9BsoUaDD7grmib2VicEJzV4TD2BEp7knXMXk/");
                    XposedBridge.log("emojiInfoObj == null  " + (emojiInfoObj == null));
                    //String content = (String)emojiInfoClass.getMethod("getContent").invoke(emojiInfoObj);
                    //XposedBridge.log("content=" + content);
                    XposedBridge.log("ok");
                }catch (Exception e) {
                    XposedBridge.log("报错了！" + e);
                }
            }
        }).start();
    }

    private void hook11() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.plugin.emoji.e.f", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "Yy", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到Yy", Toast.LENGTH_SHORT).show();
                XposedBridge.log("Yy: str=" + param.args[0]);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "Yz", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到Yz", Toast.LENGTH_SHORT).show();
                XposedBridge.log("Yz: str=" + param.args[0]);
            }
        });
        Class<?> bqClass = XposedHelpers.findClass("com.tencent.mm.storage.bq", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", Context.class, bqClass, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到a", Toast.LENGTH_SHORT).show();
                XposedBridge.log("a: str=" + param.args[2]);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "u", Context.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到u", Toast.LENGTH_SHORT).show();
                XposedBridge.log("u: str1=" + param.args[1] + "  str2" + param.args[2]);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "l", String.class, int.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到l", Toast.LENGTH_SHORT).show();
                XposedBridge.log("l: str0=" + param.args[0] + "  int1" + param.args[1] + "  int2" + param.args[2] + "  int3" + param.args[3]);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "a", String.class, String.class, int.class, int.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到a", Toast.LENGTH_SHORT).show();
                XposedBridge.log("a: str0=" + param.args[0] + "  str1" + param.args[1] + "  int2" + param.args[2] + "  int3" + param.args[3] + "  int4" + param.args[4] + "  str5" + param.args[5]);
            }
        });
        Class<?> f_aClass = XposedHelpers.findClass("com.tencent.mm.am.f$a", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", String.class, String.class, long.class, String.class, f_aClass, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到a", Toast.LENGTH_SHORT).show();
                XposedBridge.log("a: str0=" + param.args[0] + "  str1" + param.args[1] + "  int2" + param.args[2] + "  int3" + param.args[3] + "  int4" + param.args[4] + "  str5" + param.args[5]);
            }
        });
        XposedHelpers.findAndHookMethod(clazz, "YN", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "hook到YN", Toast.LENGTH_SHORT).show();
                XposedBridge.log("YN: str0=" + param.args[0]);
            }
        });
    }

    private void hook12() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.tools.ShareImgUI$a", lParam.classLoader);
        Class<?> cClass = XposedHelpers.findClass("com.tencent.mm.ui.tools.ShareImgUI$c", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "run", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Field mUriField = clazz.getDeclaredField("mUri");
                mUriField.setAccessible(true);
                Uri uri = (Uri)mUriField.get(param.thisObject);
                //Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                XposedBridge.log("uri=" + uri);
            }
        });
    }

    private void hook13() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.tools.ShareImgUI", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", clazz, Uri.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Uri uri = (Uri)param.args[1];
                String scheme = uri.getScheme();
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                XposedBridge.log("hook13: file_path=" + param.getResult());
            }
        });
        //这个是显示图片太大，发不出去
//        XposedHelpers.findAndHookMethod(clazz, "c", clazz, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Context context = (Context)param.args[0];
//                Toast.makeText(context, "hook到了", Toast.LENGTH_SHORT).show();
//                Object obj = param.args[0];
//                Method m = clazz.getDeclaredMethod("fBX");
//                m.setAccessible(true);
//                m.invoke(obj);
//            }
//        });
    }

    private void hook14() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.chatting.j", lParam.classLoader);
        Class<?> emojiInfoClass = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "c", emojiInfoClass, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String emojiInfo = (String)emojiInfoClass.getMethod("toString").invoke(param.args[0]);
                XposedBridge.log("hook14 before:  str=" + param.args[1]);
                XposedBridge.log("表情信息： ");
                XposedBridge.log(emojiInfo);
                param.args[1] = "filehelper";
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String emojiInfo = (String)emojiInfoClass.getMethod("toString").invoke(param.args[0]);
                XposedBridge.log("hook14 after:  str=" + param.args[1]);
                XposedBridge.log("表情信息： ");
                XposedBridge.log(emojiInfo);
            }
        });
    }

    private void hook15() throws Exception{
//        com.tencent.mm.ui.tools.ShareImgUI.c
//        com.tencent.mm.ui.transmit.MsgRetransmitUI.cT(String str, boolean z)
//        ((com.tencent.mm.plugin.emoji.b.d) com.tencent.mm.kernel.g.ad(com.tencent.mm.plugin.emoji.b.d.class)).getEmojiMgr().Yy(md5); //需要md5
//        获取md5
//        md5 = ((com.tencent.mm.plugin.emoji.b.d) com.tencent.mm.kernel.g.ad(com.tencent.mm.plugin.emoji.b.d.class)).getEmojiMgr().a(getApplicationContext(), new WXMediaMessage(new WXEmojiObject(this.fileName)), "")

        Class<?> wxEmojiObjectClass = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXEmojiObject", lParam.classLoader);
        Object wxEmojiObject = wxEmojiObjectClass.getConstructor(String.class).newInstance("/storage/emulated/0/Download/u=2051651383,909700034&fm=26&gp=0.jpg");
        //byte[] bytes = getNetBitmapBytes("http://emoji.qpic.cn/wx_emoji/kotBn8b6nHuMtJlZoLyxoWBTEtSczL9BsoUaDD7grmib2VicEJzV4TD2BEp7knXMXk/");
        //Object wxEmojiObject = wxEmojiObjectClass.getConstructor(byte[].class).newInstance(bytes);

        Class<?> wxMediaMessageClass = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage", lParam.classLoader);
        Class<?> wxMediaMessageClass_IMediaObjectClass = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage$IMediaObject", lParam.classLoader);
        Object wxMediaMessage = wxMediaMessageClass.getConstructor(wxMediaMessageClass_IMediaObjectClass).newInstance(wxEmojiObject);

        Class<?> gClass = XposedHelpers.findClass("com.tencent.mm.kernel.g", lParam.classLoader);
        Class<?> bdClass = XposedHelpers.findClass("com.tencent.mm.plugin.emoji.b.d", lParam.classLoader);
        Object adObj = gClass.getMethod("ad", Object.class.getClass()).invoke(null, bdClass);
        Object emojiMgr = bdClass.getMethod("getEmojiMgr").invoke(adObj);

        Class<?> adClass = XposedHelpers.findClass("com.tencent.mm.pluginsdk.a.d", lParam.classLoader);
        String field_md5 = (String)adClass.getMethod("a", Context.class, wxMediaMessageClass, String.class).invoke(emojiMgr, wxContext.getApplicationContext(), wxMediaMessage, "");

        Object emojiInfoObj = adClass.getMethod("Yy", String.class).invoke(emojiMgr, field_md5);

        Class<?> emojiInfoClass = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        Class<?> jClass = XposedHelpers.findClass("com.tencent.mm.ui.chatting.j", lParam.classLoader);
        jClass.getMethod("c", emojiInfoClass, String.class).invoke(null, emojiInfoObj, "filehelper");
    }

    private byte[] getLocalBitmapBytes(String path) throws Exception {
        FileInputStream fis = new FileInputStream(path);
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        return bytes;
    }

    private byte[] getNetBitmapBytes(String url) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (response.code() != 200) {
            throw new Exception("加载网络图片失败！" + response.message());
        }
        return response.body().bytes();
    }

    private byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }
}
