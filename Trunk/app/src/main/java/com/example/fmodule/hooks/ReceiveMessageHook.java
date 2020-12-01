package com.example.fmodule.hooks;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ReceiveMessageHook implements IXposedHookLoadPackage {
    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.tencent.mm")) {
            return;
        }
        hookContext(loadPackageParam);
        hookMessage(loadPackageParam);
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

    private void hookMessage(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "insert",
                String.class,
                String.class,
                ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "劫持到消息", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("劫持成功： args0: " + param.args[0] + "   args1: " + param.args[1]);
                        String tableName = (String) param.args[0];
                        if (tableName.isEmpty() || !tableName.equals("message")) {
                            return;
                        }
                        ContentValues contentValues = (ContentValues) param.args[2];
//                        Integer type = contentValues.getAsInteger("type");
//                        if (null == type || type != 1) {
//                            return;
//                        }

                        XposedBridge.log("遍历content里的信息：");
                        for(Map.Entry<String, Object> item : contentValues.valueSet())
                        {
                            XposedBridge.log(item.getKey() + " : " + item.getValue().toString());
                        }
                        XposedBridge.log("遍历content里的信息完成\n\n\n");
                    }
                }
        );
    }
}
