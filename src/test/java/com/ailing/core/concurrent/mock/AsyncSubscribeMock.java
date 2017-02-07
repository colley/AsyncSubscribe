/**
 * Copyright (C), 2011-2017  ColleyMa
 * File Name: AsyncSubscribeMock.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:51
 * History:
 */
package com.ailing.core.concurrent.mock;

import com.ailing.core.concurrent.AsyncSubscribe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * @author Colley
 * @version 1.0  Date: 17-2-7 下午4:51
 */
@Service("asyncSubscribeMock")
public class AsyncSubscribeMock {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @AsyncSubscribe(resultClz = int.class)
    public int waitOneMinCase() {
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        return 100;
    }

    public int withOutCase() {
        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e);
        }
        
        return 100;
    }
}
