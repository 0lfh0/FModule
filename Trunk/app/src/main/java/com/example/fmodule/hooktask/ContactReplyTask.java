package com.example.fmodule.hooktask;

import java.util.ArrayList;

public class ContactReplyTask {
    public String wxId;
    public ArrayList<ReplyTask> replyTasks;

    public ContactReplyTask() {
        wxId = "";
        replyTasks = new ArrayList<>();
    }
}
