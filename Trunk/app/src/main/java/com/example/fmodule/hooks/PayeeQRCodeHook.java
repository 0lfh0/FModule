package com.example.fmodule.hooks;

import android.content.Context;
import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PayeeQRCodeHook implements IXposedHookLoadPackage {
    private XC_LoadPackage.LoadPackageParam lParam;
    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.tencent.mm")) {
            return;
        }
        lParam = loadPackageParam;
        hookContext(loadPackageParam);
        hook1();
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
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.plugin.collect.model.b", lParam.classLoader);
        Class<?> u_aClass = XposedHelpers.findClass("com.tencent.mm.platformtools.u$a", lParam.classLoader);
        XposedHelpers.findAndHookMethod(clazz, "a", Context.class, String.class, String.class, int.class, String.class, u_aClass, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "hook到了", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("--------------------------------------");
                        XposedBridge.log("args1=" + param.args[1]);
                        XposedBridge.log("args2=" + param.args[2]);
                        XposedBridge.log("args3=" + param.args[3]);
                        XposedBridge.log("args4=" + param.args[4]);
                        XposedBridge.log("args6=" + param.args[6]);
                        param.args[4] = "hello world";
                    }
                });
    }
}
