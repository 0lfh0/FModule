package com.example.fmodule.hooks.models;

public class SendItemModel {
    //延时多少毫秒发送
    public int Delay;
    //0文本 1表情图片
    public int Type;
    //消息内容，如果这里Type为1时，也就是发表情时，填表情的链接
    public String Content;
}
