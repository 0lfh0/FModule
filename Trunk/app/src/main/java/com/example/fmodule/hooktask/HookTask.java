package com.example.fmodule.hooktask;

import java.util.ArrayList;
import java.util.HashMap;

public class HookTask {
    public String wxId;
    public String nickname;
    public DiceTask diceTask;
    public HashMap<String, ContactReplyTask> contactsReplyTask;
    public HashMap<String, ContactSendTask> contactsSendTask;
    public ArrayList<ReplyTask> globalReplyTasks;

    public HookTask() {
        wxId = "";
        nickname = "";
        diceTask = new DiceTask();
        contactsReplyTask = new HashMap<>();
        contactsSendTask = new HashMap<>();
        globalReplyTasks = new ArrayList<>();
    }
}
