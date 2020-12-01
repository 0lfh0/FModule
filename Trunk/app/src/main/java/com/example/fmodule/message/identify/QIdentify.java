package com.example.fmodule.message.identify;

import easynet.network.ARequest;

public class QIdentify extends ARequest {
    public int identity;
    public static final int wx = 1;
    public static final int other = 2;
    public String id;
    public String nickname;
    public String avatarBase64;
}
