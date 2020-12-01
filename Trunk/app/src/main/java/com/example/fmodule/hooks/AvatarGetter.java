package com.example.fmodule.hooks;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class AvatarGetter {
    private Constructor<?> cClassConstructor;
    private Method vmMethod;

    private Field wxVField;

    private Method ejMethod;

    public AvatarGetter(XC_LoadPackage.LoadPackageParam lParam) {
        try {
            String str1="V636F6D2E74656E63656E742E6D6D2E706C7567696E73646B2E75692E63F";//com.tencent.mm.pluginsdk.ui.c
            Class<?> cClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str1), lParam.classLoader);
            cClassConstructor = cClass.getConstructor(String.class, float.class);
            vmMethod = cClass.getMethod("vm", boolean.class);

            String str2="T636F6D2E74656E63656E742E6D6D2E706C7567696E73646B2E75692E6AD";//com.tencent.mm.pluginsdk.ui.j
            Class<?> jClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str2), lParam.classLoader);
            wxVField = jClass.getDeclaredField("wxV");
            wxVField.setAccessible(true);

            String str3="I636F6D2E74656E63656E742E6D6D2E706C7567696E73646B2E75692E6A2461A";//com.tencent.mm.pluginsdk.ui.j$a
            Class<?> j_aClass = XposedHelpers.findClass(HookEntryUtil.decrypt(str3), lParam.classLoader);
            //Kz是默认头像
            ejMethod = j_aClass.getMethod("ej", String.class);

        } catch (Exception e) {
            XposedBridge.log("initialize AvatarGetter error：" + e);
        }
    }

    public Bitmap getBitmap(String wxId) throws Exception {
        Object cObj = cClassConstructor.newInstance(wxId, 0.1f);
        vmMethod.invoke(cObj, false);
        Object wxVObj = wxVField.get(cObj);
        Bitmap bitmap = (Bitmap)ejMethod.invoke(wxVObj, wxId);
        return bitmap;
    }

    public byte[] getBytes(String wxId) throws Exception{
        Bitmap bitmap = getBitmap(wxId);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    public String getBase64(String wxId) throws Exception {
        byte[] bytes = getBytes(wxId);
        String avatarBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        return avatarBase64;
    }
}
