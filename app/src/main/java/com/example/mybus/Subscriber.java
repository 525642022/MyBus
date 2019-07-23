package com.example.mybus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscriber {
    /**
     * 在哪个线程执行注解方法
     */
    ThreadMode threadMode() default ThreadMode.POSTING;
}
