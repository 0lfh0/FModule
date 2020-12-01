package com.example.fmodule.hooks;

public class DecryptStrCallback {
    private int a;
    private int b;
    public DecryptStrCallback(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public String run(String str) {
        int i = 2 - a;
        int j = 2 - b;
        return str.substring(i, str.length() - j);
    }
};
