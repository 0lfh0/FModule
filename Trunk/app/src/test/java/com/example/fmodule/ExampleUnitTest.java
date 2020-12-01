package com.example.fmodule;

import com.example.fmodule.hooks.HookEntryUtil;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        LinkedList<String> strs1 = new LinkedList<>();
        LinkedList<String> strs2 = new LinkedList<>();
        LinkedList<String> strs3 = new LinkedList<>();

        strs2.add("one");
        strs3.add("two");
        strs2.add("three");
        strs3.add("four");
        strs1.addAll(strs2);
        strs1.addAll(strs3);
        System.out.println("strs1=" + Arrays.toString(strs1.toArray()));
        System.out.println("strs2=" + Arrays.toString(strs2.toArray()));
        System.out.println("strs3=" + Arrays.toString(strs3.toArray()));
//        try {
//            String str = HookEntryUtil.encrypt("com.tencent.mm.modelvoice.s");
//            System.out.println("str=" + str);
//            String original = HookEntryUtil.decrypt(str);
//            System.out.println("original=" + original);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
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