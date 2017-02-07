/**
 * File Name: ConcurrentAsyncEvent.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:27
 * History:
 */
package com.ailing.core.concurrent;

import com.google.common.eventbus.Subscribe;

import org.aopalliance.intercept.MethodInvocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author Colley
 * @version 1.0  Date: 17-2-7 下午4:27
 */
public class ConcurrentAsyncEvent {
    private static final int CORE_POOL_SIZE = 30; //corePoolSize 设置
    private static final int MAX_QUEUE_SIZE = 200;
    private static final int MAX_IMUM_POOL_SIZE = 120; //maximumPoolSize设置
    private static final long DEFAULT_KEEP_ALIVE_TIME_SECONDS = 60;
    private static final String CONCURRENT_PROC_POOL_NAME = "ConcurrentAsyncEvent";
    private static final Log logger = LogFactory.getLog(ConcurrentAsyncEvent.class);
    private static Lock lock = new ReentrantLock();

    /**
     * 用于执行同步或异步调用的线程池
     */
    private static SubscribeAsyncEventBus createAsyncEventBus = null;
    private SubscribeAsyncEventBus registerEvent;

    private ConcurrentAsyncEvent() {
        this.registerEvent = createEventBus();
        this.registerEvent.register(this);
    }

    public static ConcurrentAsyncEvent getInstance() {
        return AsyncEventHolder.INSTANCE;
    }

    public SubscribeAsyncEventBus createEventBus() {
        lock.lock();

        try {
            if (createAsyncEventBus == null) {
                ExecutorService executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_IMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME_SECONDS * 1000L,
                                                                  TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(MAX_QUEUE_SIZE),
                                                                  new NamedThreadFactory(CONCURRENT_PROC_POOL_NAME), new ExecutionRejectedHandler()
                                                                 );
                createAsyncEventBus = new SubscribeAsyncEventBus("SEMAsyncEventBus", executor);
            }
        } finally {
            lock.unlock();
        }

        return createAsyncEventBus;
    }

    public void post(ProceedingJoinPoint point, ContextHolder contextHolder) {
        registerEvent.post(point, contextHolder);
    }

    public void post(MethodInvocation invocation, ContextHolder contextHolder) {
        registerEvent.post(invocation, contextHolder);
    }

    @Subscribe
    public void process(ProceedingJoinPoint point) {
        try {
            JoinPointUtils.process(point);
        } catch (Exception e) {
            logger.error("process failed," + point.getKind(), e);
        }
    }

    @Subscribe
    public void invocation(MethodInvocation invocation) {
        try {
            invocation.proceed();
        } catch (Throwable e) {
            logger.error("invocation failed," + invocation.getMethod().getName(), e);
        }
    }

    /**
     * unregister event
     * unregister
     * @return void
     * @author mayuanchao
     * @date 2016年11月29日
     */
    public void unregister() {
        this.registerEvent.unregister(this);
    }

    public final class ExecutionRejectedHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            logger.warn("ConcurrentAsyncEvent Task[" + r.toString() + "] rejected by executor :" + executor.toString());
        }
    }

    private static class AsyncEventHolder {
        private static final ConcurrentAsyncEvent INSTANCE = new ConcurrentAsyncEvent();
    }
}
