package com.example.fmodule.hooks;

import android.content.ContentValues;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ReceiveMsgHooker {
    public ReceiveMsgHooker(XC_LoadPackage.LoadPackageParam lParam, XC_MethodHook methodHook) {
        try {
            String str1 = "L636F6D2E74656E63656E742E776364622E64617461626173652E53514C6974654461746162617365E"; //com.tencent.wcdb.database.SQLiteDatabase
            Class<?> clazz = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            XposedHelpers.findAndHookMethod(
                    clazz,
                    "insert",
                    String.class,
                    String.class,
                    ContentValues.class,
                    methodHook);
        }catch (Exception e) {
            XposedBridge.log("initialize ReceiveMsgHooker error：" + e);
        }
    }
}
