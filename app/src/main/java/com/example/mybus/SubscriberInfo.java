package com.example.mybus;

import java.lang.reflect.Method;

/**
 * 订阅对象
 */
public class SubscriberInfo {
    public Object object;
    //消息类型
    public Class<?> type;
    // 回调线程
    public ThreadMode threadMode;
    //回调方法
    public Method method;


    public SubscriberInfo(Class<?> type, ThreadMode threadMode, Method method,Object object) {
        this.type = type;
        this.threadMode = threadMode;
        this.method = method;
        this.object = object;
    }
}
