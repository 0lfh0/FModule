package com.example.fmodule.hooks.models;

//自动回复消息请求远程接口时的携带参数
public class RecvModel {
    //来自哪个好友的消息
    public String FromUser;
    //此消息ID
    public int MsgId;
    //消息内容
    public String Content;
    public int Status;
    //时间
    public long CreateTime;
    public int IsSend;
    //消息类型，比如文本、图片、表情等等
    public int Type;
}
