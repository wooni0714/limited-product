package com.limited.product.common.aop;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String key();

    long waitTime() default 1L;

    long leaseTime() default 3L;

    TimeUnit unit() default TimeUnit.SECONDS;
}