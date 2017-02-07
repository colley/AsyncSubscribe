/**
 * Copyright (C), 2011-2017  ColleyMa
 * File Name: BaseTestCase.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:48
 * History:
 */
package com.ailing.core.concurrent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


/**
 * @author ColleyMa
 * @version 1.0  Date: 17-2-7 下午4:48
 */
@ContextConfiguration(locations =  {
    "classpath*:/applicationContext.xml"}
)
public abstract class BaseTestCase extends AbstractJUnit4SpringContextTests {
    static {
        System.out.println(System.getProperty("os.name"));

        String osName = System.getProperty("os.name");

        if ((osName != null) && osName.toLowerCase().startsWith("win")) {
            // my configuration file
            System.setProperty("global.config.path", "D:/env/");
        } else {
            System.setProperty("global.config.path", "/var/www/webapps/config");
        }

        Logger semLogIbatis = Logger.getLogger("com.ailing.concurrent");
        semLogIbatis.setLevel(Level.DEBUG);
    }

    protected Log log = LogFactory.getLog(this.getClass());
}
