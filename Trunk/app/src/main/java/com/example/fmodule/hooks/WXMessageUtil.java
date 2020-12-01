package com.example.fmodule.hooks;

public class WXMessageUtil {
    public static final int[] types = new int[] {
            1, //文本消息
            3, //图片消息
            34, //语音消息
            37, //好友确认消息
            40, //POSSIBLEFRIEND_MSG
            42, //共享名片
            43, //视频消息
            47, //动画表情
            48, //位置消息
            49, //分享链接
            50, //VOIPMSG
            51, //微信初始化消息
            52, //VOIPNOTIFY
            53, //VOIPINVITE
            62, //小视频
            9999, //SYSNOTICE
            10000, //系统消息
            10002, //撤回消息
            318767153, //收款信息
    };

    public static boolean containsType(int type) {
        for (int i=0; i<types.length; i++) {
            if (types[i] == type) {
                return true;
            }
        }
        return false;
    }
}
