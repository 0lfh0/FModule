package com.example.fmodule.hooks;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookDemo implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log(loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            XposedBridge.log("开始劫持");
            //hook0(loadPackageParam);
            //hook2(loadPackageParam);
            //hook3(loadPackageParam);
            //hook4(loadPackageParam);
            hook6(loadPackageParam);
        }
    }

    private void hook0(XC_LoadPackage.LoadPackageParam loadPackageParam) throws  Throwable {
        final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", loadPackageParam.classLoader);
        Class clazz = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.chatting.v");
        XposedHelpers.findAndHookMethod(clazz, "B", emojiInfo, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Method m = emojiInfo.getMethod("toString");
                String s = (String)m.invoke(param.args[0]);
                XposedBridge.log("劫持到v.B之前的emojiInfo： " + s);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Method m = emojiInfo.getMethod("toString");
                String s = (String)m.invoke(param.args[0]);
                XposedBridge.log("劫持到v.B之后的emojiInfo： " + s);
            }
        });
    }

    private void hook1(XC_LoadPackage.LoadPackageParam loadPackageParam){
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.emoji.panel.a.h$b", loadPackageParam.classLoader);
        Class<?> y = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.y", loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                Context.class,
                int.class,
                y,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("劫持到b.a之前: i = " + (int)param.args[1]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("劫持到b.a之后: i = " + (int)param.args[1]);
                    }
                }
        );
    }

    private void hook2(final XC_LoadPackage.LoadPackageParam lParam) {
        final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        final Class<?> y = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.y", lParam.classLoader);
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.emoji.panel.a.d", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                Context.class,
                int.class,
                y,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Class<?> hClass = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.h", lParam.classLoader);
                        Field fUeField = hClass.getField("fUe");
                        Object fUeObj = fUeField.get(param.args[2]);
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(fUeObj);

                        XposedBridge.log("劫持到a.d.a之前: hVar.fUe = " + s);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                }
        );
    }

    private void hook3(final XC_LoadPackage.LoadPackageParam lParam) {
        final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        //final Class<?> y = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.y", lParam.classLoader);
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.cc.a", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "p",
                emojiInfo,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.args[0]);

                        XposedBridge.log("劫持到cc.a.p之前: emojiInfo = " + s);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.args[0]);
                        XposedBridge.log("劫持到cc.a.p之后: emojiInfo = " + s);
                        String ss = (String)m.invoke(param.getResult());
                        XposedBridge.log("劫持到cc.a.p之后(返回结果): emojiInfo = " + ss);
                    }
                }
        );
    }

    private void hook4(final XC_LoadPackage.LoadPackageParam lParam) {
        final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        //final Class<?> y = XposedHelpers.findClass("com.tencent.mm.emoji.a.a.y", lParam.classLoader);
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.plugin.emoji.e.f", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "p",
                emojiInfo,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.args[0]);

                        XposedBridge.log("劫持到e.f.p之前: emojiInfo = " + s);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.args[0]);
                        XposedBridge.log("劫持到e.f.p之后: emojiInfo = " + s);
                        String ss = (String)m.invoke(param.getResult());
                        XposedBridge.log("劫持到e.f.p之后(返回结果): emojiInfo = " + ss);
                    }
                }
        );
    }

    private void hook5(final XC_LoadPackage.LoadPackageParam lParam) {
        final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        final Class<?> cClass = XposedHelpers.findClass("android.database.Cursor", lParam.classLoader);
        final Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.g.c.bi", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "convertFrom",
                cClass,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Field f = clazz.getField("field_name");
                        if (!((String)f.get(param.thisObject)).contains("dice"))
                        {
                            return;
                        }
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.thisObject);

                        XposedBridge.log("劫持到convertFrom之前: emojiInfo = " + s);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Field f = clazz.getField("field_name");
                        if (!((String)f.get(param.thisObject)).contains("dice"))
                        {
                            return;
                        }
                        Method m = emojiInfo.getMethod("toString");
                        String s = (String)m.invoke(param.thisObject);
                        XposedBridge.log("劫持到劫持到convertFrom之前之后: emojiInfo = " + s);
                    }
                }
        );
    }

    private void hook6(final XC_LoadPackage.LoadPackageParam lParam) {
        //final Class<?> emojiInfo = XposedHelpers.findClass("com.tencent.mm.storage.emotion.EmojiInfo", lParam.classLoader);
        //final Class<?> cClass = XposedHelpers.findClass("android.database.Cursor", lParam.classLoader);
        final Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.bs", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "jt",
                int.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("劫持到jt之前: i1 = " + (int)param.args[0] + "  i2 = " + (int)param.args[1]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("劫持到jt之后: i1 = " + (int)param.args[0] + "  i2 = " + (int)param.args[1]);
                        XposedBridge.log("劫持到jt之后(返回结果): 结果 = " + (int)param.getResult());
                        param.setResult(0);
                    }
                }
        );
    }
}
