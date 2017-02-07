/**
 * File Name: JoinPointUtils.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:26
 * History:
 */
package com.ailing.core.concurrent;

import org.aspectj.lang.ProceedingJoinPoint;


/**
 * @author Colley
 * @version 1.0  Date: 17-2-7 下午4:25
 */
public class JoinPointUtils {
    public static Object process(ProceedingJoinPoint point, Object[] args)
                          throws Exception {
        try {
            return point.proceed(args);
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }

    public static Object process(ProceedingJoinPoint point)
                          throws Exception {
        try {
            return point.proceed();
        } catch (Throwable e) {
            throw new Exception(e);
        }
    }
}
