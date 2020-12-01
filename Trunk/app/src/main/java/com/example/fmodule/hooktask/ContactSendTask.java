package com.example.fmodule.hooktask;

import java.util.ArrayList;

public class ContactSendTask {
    public String wxId;
    public ArrayList<SendTask> sendTasks;

    public ContactSendTask() {
        wxId = "";
        sendTasks = new ArrayList<>();
    }
}
