package com.example.fmodule.message;


import com.example.fmodule.message.contacts.PContacts;
import com.example.fmodule.message.contacts.QContacts;
import com.example.fmodule.message.hooktask.MDiceTask;
import com.example.fmodule.message.hooktask.MGlobalReplyTasks;
import com.example.fmodule.message.hooktask.MHookTask;
import com.example.fmodule.message.hooktask.MReplyTask;
import com.example.fmodule.message.hooktask.MSendTask;
import com.example.fmodule.message.hooktask.MVoiceLenTask;
import com.example.fmodule.message.identify.PIdentify;
import com.example.fmodule.message.identify.QIdentify;
import com.example.fmodule.message.useravatar.PUserAvatar;
import com.example.fmodule.message.useravatar.QUserAvatar;
import com.example.fmodule.message.wxuserinfo.PWXUserInfo;
import com.example.fmodule.message.wxuserinfo.QWXUserInfo;

import easynet.network.MessageMgr;

public class MessageManifest extends MessageMgr {

    public MessageManifest() {
        registerMessage((short)1, QIdentify.class);
        registerMessage((short)2, PIdentify.class);
        registerMessage((short)3, QWXUserInfo.class);
        registerMessage((short)4, PWXUserInfo.class);
        registerMessage((short)5, MHookTask.class);
        registerMessage((short)6, MDiceTask.class);
        registerMessage((short)7, QContacts.class);
        registerMessage((short)8, PContacts.class);
        registerMessage((short)9, MReplyTask.class);
        registerMessage((short)10, MGlobalReplyTasks.class);
        registerMessage((short)11, MSendTask.class);
        registerMessage((short)12, QUserAvatar.class);
        registerMessage((short)13, PUserAvatar.class);
        registerMessage((short)14, MVoiceLenTask.class);
    }
}
