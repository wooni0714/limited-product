package com.limited.product.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private final RedissonClient redissonClient;
    private final AopTransactionAspect aopTransactionAspect;
    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    @Around("@annotation(com.limited.product.common.aop.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = createLock(distributedLock, methodSignature, joinPoint);
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean isTriedLock = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.unit());
            if (!isTriedLock) {
                return false;
            }

            return aopTransactionAspect.proceed(joinPoint);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalStateException e) {
                log.warn("Unlock failed name = {}, key = {}", method.getName(), key);
            }
        }
    }

    private String createLock(DistributedLock distributedLock, MethodSignature methodSignature, JoinPoint joinPoint) {
        return REDISSON_LOCK_PREFIX + SpELExpressionEvaluator.evaluate(
                methodSignature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key());
    }
}
