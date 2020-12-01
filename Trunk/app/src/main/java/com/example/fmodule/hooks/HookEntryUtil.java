package com.example.fmodule.hooks;

import java.util.Random;

public class HookEntryUtil {

    public static EncryptStrCallback encryptStrCallback;

    public static DecryptStrCallback decryptStrCallback;

    public interface EncryptStrCallback {
        String run(String str);
    }

//    public interface DecryptStrCallback {
//        String run(String str);
//    }

    public static String encrypt(String str) throws Exception{
        if (encryptStrCallback == null) {
            return "";
        }
        String hexStr = bytesToHexString(str.getBytes("ASCII"));
        return encryptStrCallback.run(hexStr);
    }

    public static String decrypt(String str) throws Exception{
        if (decryptStrCallback == null) {
            return "";
        }
        str = decryptStrCallback.run(str);
        byte[] bytes = hexStringToByte(str);
        String originalStr = new String(bytes, "ASCII");
        return originalStr;
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
