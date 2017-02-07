/**
 * Copyright NewHeight Co.,Ltd. (C)2016
 * File Name: NamedThreadFactory.java
 * Encoding: UTF-8
 * Date: 16-11-17 下午1:10
 * History:
 */
package com.ailing.core.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author mayuanchao
 * @version 1.0  Date: 16-11-17 下午1:10
 */
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public NamedThreadFactory(String theadGroupName) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = theadGroupName + "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }
}
