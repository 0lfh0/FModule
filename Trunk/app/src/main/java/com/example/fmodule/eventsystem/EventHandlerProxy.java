package com.example.fmodule.eventsystem;

import java.lang.reflect.Method;

public class EventHandlerProxy {
    public Object object;
    public Method handler;

    public void invoke(Object... args) throws Exception {
        if (handler.getParameterTypes().length != args.length) {
            return;
        }
        if (handler != null) {
            handler.invoke(object, args);
        }
    }
}
