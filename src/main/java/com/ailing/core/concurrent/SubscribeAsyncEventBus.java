/**
 * File Name: SubscribeAsyncEventBus.java
 * Encoding: UTF-8
 * Date: 16-11-25 下午1:31
 * History:
 */
package com.ailing.core.concurrent;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.Executor;


/**
 * @author colley
 * @version 1.0  Date: 16-11-25 下午1:31
 */
public class SubscribeAsyncEventBus extends AsyncEventBus {
    public SubscribeAsyncEventBus(Executor executor) {
        super(executor);
    }

    public SubscribeAsyncEventBus(String identifier, Executor executor) {
        super(identifier, executor);
    }

    public void post(Object event, ContextHolder contextHolder) {
        //设置本地变量
        ContextHolder.setContext(contextHolder);
        super.post(event);
    }
}
