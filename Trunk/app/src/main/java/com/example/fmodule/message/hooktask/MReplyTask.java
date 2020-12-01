package com.example.fmodule.message.hooktask;

import com.example.fmodule.hooktask.ReplyTask;

import easynet.network.AMessage;

public class MReplyTask extends AMessage {
    public enum OpType {add, update, delete}
    public OpType opType;
    public ReplyTask replyTask;
}
