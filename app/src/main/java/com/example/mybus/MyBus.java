package com.example.mybus;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBus {
    private static volatile MyBus myBus;
    //保存带注解的方法
    private Map<Object, List<SubscriberInfo>> cacheMap = new HashMap<>();
    private ExecutorService executorService;
    private Handler handler;

    private MyBus() {
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    //单例初始化
    public static MyBus getInstance() {
        if (myBus == null) {
            synchronized (MyBus.class) {
                if (myBus == null) {
                    myBus = new MyBus();
                }
            }
        }
        return myBus;
    }

    /***
     * 注册方法
     * @param obj
     */
    public void register(Object obj) {
        List<SubscriberInfo> list = cacheMap.get(obj);
        if (list == null) {
            list = findAnnotationMethod(obj);
            cacheMap.put(obj, list);
        }
    }

    /**
     * 寻找带注解的方法
     *
     * @param obj
     * @return
     */
    private List<SubscriberInfo> findAnnotationMethod(Object obj) {
        List<SubscriberInfo> list = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        //获取所有的方法
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Subscriber subscriber = method.getAnnotation(Subscriber.class);
            if (subscriber == null) continue;
            checkMethod(method);
            SubscriberInfo subscriberInfo = new SubscriberInfo(method.getParameterTypes()[0], subscriber.threadMode(), method, obj);
            list.add(subscriberInfo);
        }
        return list;
    }

    /**
     * 检查方法是否符合规则
     *
     * @param method
     */
    private void checkMethod(Method method) {
        //方法返回必须为void
        if (!TextUtils.equals("void", method.getGenericReturnType().toString())) {
            throw new RuntimeException("method must return void ");
        }
        //方法参数校验
        Class<?>[] paramsTypes = method.getParameterTypes();
        if (paramsTypes.length != 1) {
            throw new RuntimeException("method must has one params");
        }
    }

    /**
     * 清除保存的注解
     *
     * @param getter
     */
    public void unRegister(Object getter) {
        if (cacheMap.containsKey(getter)) {
            cacheMap.remove(getter);
        }
    }

    /**
     * 发送信息
     *
     * @param send
     */
    public static void post(Object send) {
        Set<Object> set = myBus.cacheMap.keySet();
        for (final Object obj : set) {
            List<SubscriberInfo> subscriberInfos = myBus.cacheMap.get(obj);
            if (subscriberInfos != null) {
                for (final SubscriberInfo subscriberInfo : subscriberInfos) {
                    //判断这个类是否为SubscriberInfo的子类
                    if (subscriberInfo.type.isAssignableFrom(send.getClass())) {
                        execute(subscriberInfo, send);
                    }
                }
            }
        }
    }

    private static void execute(final SubscriberInfo subscriberInfo, final Object send) {
        switch (subscriberInfo.threadMode) {
            case MAIN:
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    invoke(subscriberInfo, send);
                } else {
                    myBus.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invoke(subscriberInfo, send);
                        }
                    });
                }
                break;
            case POSTING:
                invoke(subscriberInfo, send);
                break;
            case BACKGROUND:
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    myBus.executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            invoke(subscriberInfo, send);
                        }
                    });
                } else {
                    invoke(subscriberInfo, send);
                }
                break;
            case ASYNC:
                myBus.executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(subscriberInfo, send);
                    }
                });
                break;
        }

    }

    /**
     * 执行注解方法
     *
     * @param info   方法封装对象
     * @param setter 消息对象的封装
     */
    private static void invoke(SubscriberInfo info, Object setter) {
        try {
            info.method.setAccessible(true);
            info.method.invoke(info.object, setter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
