package com.example.fmodule.hooks.models;

import java.util.List;

public class SendModel {
    //是否发送，如果false则不发送
    public boolean IsSend;
    //发送对象的微信号
    public String ToUser;
    //SendItemModel为发送内容，这里表示可以发送多条消息
    public List<SendItemModel> SendList;
}
