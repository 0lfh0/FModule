package com.example.fmodule.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DiceHooker {
    public DiceHooker(XC_LoadPackage.LoadPackageParam lParam,  XC_MethodHook methodHook) {
        try {
            String str1 = "C636F6D2E74656E63656E742E6D6D2E73646B2E706C6174666F726D746F6F6C732E6273M"; //com.tencent.mm.sdk.platformtools.bs
            final Class<?> clazz = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            XposedHelpers.findAndHookMethod(clazz,"jt", int.class, int.class, methodHook);
        }catch (Exception e) {
            XposedBridge.log("initialize DiceHooker error：" + e);
        }
    }
}
