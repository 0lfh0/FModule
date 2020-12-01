package com.example.fmodule.hooks;

import java.lang.reflect.Method;
import java.util.Map;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class UserInfoGetter {
    private Method ayzMethod;
    private Method ayyMethod;
    private Method azpMethod;
    public UserInfoGetter(XC_LoadPackage.LoadPackageParam lParam) {
        try {
            String str1 = "C636F6D2E74656E63656E742E6D6D2E6D6F64656C2E75T"; //com.tencent.mm.model.u
            Class<?> uClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            ayzMethod = uClass.getMethod("ayz");
            ayyMethod = uClass.getMethod("ayy");
            azpMethod = uClass.getMethod("azp");
        }catch (Exception e) {
            XposedBridge.log("initialize UserInfoGetter error: " + e);
        }
    }

    public String getWxId() throws Exception{
        return (String)ayzMethod.invoke(null);
    }

    public String getNickname() throws Exception{
        return (String)ayyMethod.invoke(null);
    }

    public Map<String, String> getLastInfo() throws Exception{
        return (Map<String, String>)azpMethod.invoke(null);
    }
}
