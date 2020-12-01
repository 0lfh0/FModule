package com.example.fmodule.hooks;

import android.content.Context;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SendVoiceHook implements IXposedHookLoadPackage {
    private XC_LoadPackage.LoadPackageParam lParam;
    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        //XposedBridge.log(loadPackageParam.packageName);
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

    private void hook1() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.pluginsdk.ui.chat.ChatFooter", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "eWo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "调用了eWo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hook2() {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.modelvoice.s", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "at", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Toast.makeText(wxContext, "调用了at", Toast.LENGTH_SHORT).show();
                XposedBridge.log("arg0=" + param.args[0] + "  arg1=" + param.args[1]);
                param.args[1] = 1000;
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                param.setResult(false);
            }
        });
    }
}
