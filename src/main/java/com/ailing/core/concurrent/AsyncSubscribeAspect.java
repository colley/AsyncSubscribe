/**
 * Copyright (C), 2011-2017 The Store
 * File Name: AsyncSubscribeAspect.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:44
 * History:
 */
package com.ailing.core.concurrent;

import com.ailing.core.common.ReflectUtils;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;


/**
 * 异步调用注解
 * @author mayuanchao
 * @version 1.0  Date: 16-11-17 下午2:16
 */
@Aspect
public class AsyncSubscribeAspect implements MethodInterceptor {
    private static final Log logger = LogFactory.getLog(AsyncSubscribeAspect.class);

    @Around(value = "@annotation(AsyncSubscribe)", argNames = "AsyncSubscribe")
    public Object aroundProcess(final ProceedingJoinPoint point, final AsyncSubscribe asyncSubscribe)
                         throws Exception {
        if (logger.isWarnEnabled()) {
            logger.warn("Async callback start,class is " + point.getClass().getName() + ",kind=" + point.getKind());
        }

        ContextHolder contextHolder = ContextHolder.getContext();
        ConcurrentAsyncEvent asyncEventTask = ConcurrentAsyncEvent.getInstance();
        asyncEventTask.post(point, contextHolder);

        @SuppressWarnings("rawtypes")
        Class resultClas = asyncSubscribe.resultClz();

        if (logger.isWarnEnabled()) {
            logger.warn((("Async callback start,resultClas is " + resultClas) != null) ? resultClas.getName() : "");
        }

        CLASS_TYPE clazzType = CLASS_TYPE.getClazType(resultClas);

        if (clazzType != null) {
            return clazzType.result;
        }

        return null;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method m = invocation.getMethod();
        Class<?> clazz = m.getDeclaringClass();
        Class<?> classToUse = null;
        AsyncSubscribe vs = clazz.getAnnotation(AsyncSubscribe.class);
        classToUse = clazz;

        if (vs == null) {
            Class<?> targetClazz = ReflectUtils.getFieldValue(invocation, "target").getClass();
            classToUse = targetClazz;
            vs = targetClazz.getAnnotation(AsyncSubscribe.class);

            if (vs == null) {
                Class<?>[] interfaces = targetClazz.getInterfaces();

                for (Class<?> inf : interfaces) {
                    vs = inf.getAnnotation(AsyncSubscribe.class);

                    if (vs != null) {
                        classToUse = inf;

                        break;
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("invoke : " + classToUse.getName() + "," + m.getName());
        }

        if (vs == null) {
            return invocation.proceed();
        }

        if (logger.isWarnEnabled()) {
            logger.warn("Async callback start,class is " + classToUse.getName() + ",kind=" + m.getName());
        }

        ContextHolder contextHolder = ContextHolder.getContext();
        ConcurrentAsyncEvent asyncEventTask = ConcurrentAsyncEvent.getInstance();
        asyncEventTask.post(invocation, contextHolder);

        return null;
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static enum CLASS_TYPE {
    	 INT(int.class, 0), 
    	 DOUBLE(double.class, 0d), 
    	 LONG(long.class, 0L), 
    	 FLOAT(float.class, 0f),
    	 BYTE(byte.class, 0), 
    	 SHORT(short.class, 0), 
         CHAR(char.class, 0),
         BOOLEAN(boolean.class, true);
    	
    	private Class clazz;
        private Object result;

        CLASS_TYPE(Class clazz, Object result) {
            this.clazz = clazz;
            this.result = result;
        }

        public static CLASS_TYPE getClazType(Class clz) {
            if (clz != null) {
                for (CLASS_TYPE clazzType : CLASS_TYPE.values()) {
                    if (clz.isAssignableFrom(clazzType.clazz)) {
                        return clazzType;
                    }
                }
            }

            return null;
        }
       
    }
}
