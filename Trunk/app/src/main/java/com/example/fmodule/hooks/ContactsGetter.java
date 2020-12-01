package com.example.fmodule.hooks;

import android.database.Cursor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ContactsGetter {
    private Method axAMethod;
    private Method aMethod;
    private String arg0;
    private String arg1;
    private List<String> arg2;
    private LinkedList<String> arg3;
    private boolean arg4;
    private boolean arg5;
    public ContactsGetter(XC_LoadPackage.LoadPackageParam lParam) {
        try {
            String str1 = "X636F6D2E74656E63656E742E6D6D2E6D6F64656C2E63R"; //com.tencent.mm.model.c
            Class<?> cClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            axAMethod = cClass.getMethod("axA");

            String str2 = "V636F6D2E74656E63656E742E6D6D2E73746F726167652E626CM"; //com.tencent.mm.storage.bl
            Class<?> blClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str2), lParam.classLoader);
            aMethod = blClass.getMethod("a", String.class, String.class, List.class, List.class, boolean.class, boolean.class);

            //String str3 = "K40616C6C2E636F6E746163742E776974686F75742E63686174726F6F6D2E776974686F75742E6F70656E696DV"; //@all.contact.without.chatroom.without.openim
            arg0 = "@all.contact";
            arg1 = null;
            arg2 = new ArrayList<>();
            arg2.add("tmessage");
            arg2.add("officialaccounts");
            arg2.add("helper_entry");
            arg2.add("blogapp");
            arg3 = new LinkedList<>();
            arg3.offer("weixin");
            arg4 = true;
            arg5 = true;
        }catch (Exception e) {
            XposedBridge.log("initialize ContactsGetter error： " + e);
        }
    }

    public Cursor get() throws Exception{
        Object blObj = axAMethod.invoke(null);
        Object aObj = aMethod.invoke(blObj, arg0, arg1, arg2, arg3, arg4, arg5);
        Cursor cursor = (Cursor)aObj;
        return cursor;
    }
}
