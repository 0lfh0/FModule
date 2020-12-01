package com.example.fmodule.hooks;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ContactsHook implements IXposedHookLoadPackage {

    private Context wxContext;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals("com.tencent.mm")) {
            return;
        }
        hookContext(loadPackageParam);
        hook3(loadPackageParam);
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

    private void hookContacts(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.storagebase.f", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                String.class,
                String[].class,
                String.class,
                String[].class,
                String.class,
                String.class,
                String.class,
                int.class,
                new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("劫持到了：");
                        XposedBridge.log("args[0]: " + param.args[0]);
                        XposedBridge.log("args[1]: " + Arrays.toString((String[])param.args[1]));
                        XposedBridge.log("args[2]: " + param.args[2]);
                        XposedBridge.log("args[3]: " + Arrays.toString((String[])param.args[3]));
                        XposedBridge.log("args[4]: " + param.args[4]);
                        XposedBridge.log("args[5]: " + param.args[5]);
                        XposedBridge.log("args[6]: " + param.args[6]);
                        XposedBridge.log("args[7]: " + (int)param.args[7]);
                    }
                }
        );
    }

    private void hook1(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.MainTabUI$TabsAdapter", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "onPageSelected",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "劫持到onPageSelected, i=" + param.args[0], Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void hook2(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.plugin.report.service.g", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "kvStat",
                int.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "劫持到kvStat, i=" + param.args[0] + "  str= " + param.args[1], Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void hook3(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.storage.aq", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "a",
                String.class,
                String.class,
                List.class,
                List.class,
                boolean.class,
                boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        //Toast.makeText(wxContext, "调用了aq.a", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("调用了aq.a=====================");
                        XposedBridge.log("args[0]: " + param.args[0]);
                        XposedBridge.log("args[1]: " + param.args[1]);
                        XposedBridge.log("args[2]: " + Arrays.toString(((List<String>)param.args[2]).toArray()));
                        XposedBridge.log("args[3]: " + Arrays.toString(((List<String>)param.args[3]).toArray()));
                        XposedBridge.log("args[4]: " + param.args[4]);
                        XposedBridge.log("args[5]: " + param.args[5]);
                        param.args[0] = "@all.contact";
                    }
                }
        );
    }

    private void hook4(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.contact.a", lParam.classLoader);
        XposedHelpers.findAndHookConstructor(
                clazz,
                Context.class,
                String.class,
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "调用了构造方法", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("args[1]: " + param.args[1]);
                        XposedBridge.log("args[2]: " + param.args[2]);
                        XposedBridge.log("args[3]: " + param.args[3]);

                        Object blObj = XposedHelpers.findClass("com.tencent.mm.model.c", lParam.classLoader).getMethod("axA").invoke(null);
                        Class<?> blClass = XposedHelpers.findClass("com.tencent.mm.storage.bl", lParam.classLoader);
                        Method aMethod = blClass.getMethod("a", String.class, String.class, List.class, List.class, boolean.class, boolean.class);
                        List<String> strList = new ArrayList<>();
                        strList.add("tmessage");
                        strList.add("officialaccounts");
                        strList.add("helper_entry");
                        strList.add("blogapp");
                        LinkedList<String> llist = new LinkedList<>();
                        llist.offer("weixin");
                        Object aObj = aMethod.invoke(blObj, "@all.contact.without.chatroom.without.openim", null, strList, llist, true, true);
                        Cursor cursor = (Cursor)aObj;
                        int count = cursor.getCount();
                        for(int i = 0; i<count; i++) {
                            cursor.moveToPosition(i);
                            XposedBridge.log("username=" + cursor.getString(0) + "; nickname=" + cursor.getString(1) + "; alias=" + cursor.getString(2));
                        }
                    }
                }
        );
    }

    private void hook5(XC_LoadPackage.LoadPackageParam lParam) {
        Class<?> clazz = XposedHelpers.findClass("com.tencent.mm.ui.contact.a", lParam.classLoader);
        XposedHelpers.findAndHookMethod(
                clazz,
                "ih",
                List.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Toast.makeText(wxContext, "调用了ih", Toast.LENGTH_SHORT).show();
                        XposedBridge.log("调用ih之前：" + Arrays.toString(((List<String>)param.args[0]).toArray()));
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("调用ih之后：" + Arrays.toString(((List<String>)param.args[0]).toArray()));
                    }
                }
        );
    }
}
