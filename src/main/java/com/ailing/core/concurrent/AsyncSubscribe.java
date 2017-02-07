/**
 * Copyright (C), 2011-2017 The Store
 * File Name: AsyncSubscribe.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:34
 * History:
 */
package com.ailing.core.concurrent;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步调度注解
 * @author Colley
 */
@Documented
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncSubscribe {
	
	public abstract String serviceName() default "AsyncSubscribe";
    
	@SuppressWarnings("rawtypes")
    public abstract Class resultClz();
}
