package com.example.fmodule.hooks;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class VoiceHooker {
    public VoiceHooker(XC_LoadPackage.LoadPackageParam lParam, XC_MethodHook methodHook) {
        try {
            String str1 = "Q636F6D2E74656E63656E742E6D6D2E6D6F64656C766F6963652E73N"; //com.tencent.mm.modelvoice.s
            Class<?> clazz = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            XposedHelpers.findAndHookMethod(clazz, "at", String.class, int.class, methodHook);
        }catch (Exception e) {
            XposedBridge.log("initialize VoiceHooker error：" + e);
        }
    }
}
