package com.example.fmodule.eventsystem;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class EventSystem {
    private final String TAG = "EventSystem";
    private static EventSystem instance;
    private LinkedList<EventHandlerProxy> handlerCache = new LinkedList<>();
    private HashMap<String, ArrayList<EventHandlerProxy>> handlers = new HashMap<>();
    private HashMap<Object, HashMap<String, EventHandlerProxy>> objHandlers = new HashMap<>();

    private EventSystem() {

    }

    public static EventSystem getInstance() {
        if (instance == null) {
            instance = new EventSystem();
        }
        return instance;
    }

    public void register(Activity object) {
        HashMap<String, EventHandlerProxy> objHandlersMap = objHandlers.get(object);
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();
        for (int i=0; i<methods.length; i++ ){
            Method method = methods[i];
            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null) {
                continue;
            }
            String eventId = eventHandler.value();
            if (eventId.trim().isEmpty()) {
                Log.e(TAG, "register: " + "注册事件失败！" + clazz.getName() + "." + method.getName() + " 事件ID不能为空");
                return;
            }
            ArrayList<EventHandlerProxy> handlerList = handlers.get(eventId);
            if (handlerList == null) {
                handlerList = new ArrayList<>();
                handlers.put(eventId, handlerList);
            }
            if (objHandlersMap == null) {
                objHandlersMap = new HashMap<>();
                objHandlers.put(object, objHandlersMap);
            }
            if (objHandlersMap.containsKey(eventId)) {
                Log.e(TAG, "register: " + "注册事件失败！" + clazz.getName() + "." + method.getName() + " 事件ID已在该类中注册");
                return;
            }
            //生成事件代理处理器
            EventHandlerProxy handler = handlerCache.poll();
            if (handler == null) {
                handler = new EventHandlerProxy();
            }
            handler.object = object;
            handler.handler = method;


            objHandlersMap.put(eventId, handler);
            handlerList.add(handler);
        }
    }

    public void unregister(Activity object) {
        HashMap<String, EventHandlerProxy> objHs = objHandlers.get(object);
        if (objHs == null) {
            return;
        }
        for (String eventId : objHs.keySet()) {
            ArrayList<EventHandlerProxy> list = handlers.get(eventId);
            if (list == null) {
                continue;
            }
            EventHandlerProxy proxy = objHs.get(eventId);
            list.remove(proxy);
            handlerCache.offer(proxy);
        }
        objHandlers.remove(object);
    }

    public void run(String eventId, Object... args) {
        ArrayList<EventHandlerProxy> list = handlers.get(eventId);
        if (list == null) {
            return;
        }
        Iterator<EventHandlerProxy> iterator = list.iterator();
        while (iterator.hasNext()) {
            EventHandlerProxy proxy = iterator.next();
            try {
                proxy.invoke(args);
            }catch (Exception e) {
                Log.e(TAG, "run: ", e);
            }
        }
    }
}
