/**
 * Copyright (C), 2011-2017 The Store
 * File Name: ReflectUtils.java
 * Encoding: UTF-8
 * Date: 17-2-7 下午4:31
 * History:
 */
package com.ailing.core.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * @author Colley
 * @version 1.0  Date: 17-2-7 下午4:31
 */
public class ReflectUtils {
    private static final Log log = LogFactory.getLog(ReflectUtils.class);

    /**
     * <p>get field value on pojo(getXxx)</p>
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getValue(Object obj, String fieldName) {
        return operate(obj, fieldName, null, ReflectType.GET);
    }

    /**
     * set field value on pojo(setXxx)
     * @param obj
     * @param fieldName
     * @param fieldVal
     */
    public static void setValue(Object obj, String fieldName, Object fieldVal) {
        operate(obj, fieldName, fieldVal, ReflectType.SET);
    }

    /**
     * using reflect on the target pojo to set/get field value(setXxx/getXxx).
     * @param obj
     * @param fieldName
     * @param fieldVal
     * @param type
     * @return
     */
    public static Object operate(Object obj, String fieldName, Object fieldVal, ReflectType type) {
        Object ret = null;

        try {
            Class<?extends Object> classType = obj.getClass();

            /**
             * a stupid way to get the original class that enhanced by CGLIB
             * Wayne Wan added by 2011-11-08
             */
            if (classType.getName().toUpperCase().indexOf("CGLIB") > -1) {
                String clazz = classType.getName();
                clazz = clazz.substring(0, clazz.indexOf("$$"));
                classType = Class.forName(clazz);
            }

            Field[] fields = classType.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];

                if (field.getName().toUpperCase().equals(fieldName.toUpperCase())) {
                    String firstLetter = fieldName.substring(0, 1).toUpperCase();

                    if (type == ReflectType.SET) {
                        String setMethodName = "set" + firstLetter + fieldName.substring(1);
                        Method setMethod = classType.getMethod(setMethodName, new Class[] { field.getType() });
                        ret = setMethod.invoke(obj, new Object[] { fieldVal });
                    }

                    if (type == ReflectType.GET) {
                        String getMethodName = "get" + firstLetter + fieldName.substring(1);
                        Method getMethod = classType.getMethod(getMethodName, new Class[] {  });
                        ret = getMethod.invoke(obj, new Object[] {  });
                    }

                    return ret;
                }
            }
        } catch (Exception e) {
            log.error("reflect failed on [" + fieldName + "] of " + obj, e);
        }

        return ret;
    }

    /**
     * invoke a method (including the private method)
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);

        if (method == null) {
            log.error("Could not find method [" + methodName + "] on target [" + object + "]");
        }

        method.setAccessible(true);

        try {
            return method.invoke(object, parameters);
        } catch (Exception e) {
            log.error("invoke method [" + methodName + "] on [" + object + "] failed!", e);
        }

        return null;
    }

    /**
     * set field value on the specified object including the private one
     * @param object
     * @param fieldName
     * @param value
     */
    public static void setFieldValue(Object object, String fieldName, Object value) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            log.error("set field [" + fieldName + "] value on [" + object + "] failed!", e);
        }
    }

    /**
     * get field value on the specified object including the private one
     * @param object
     * @param fieldName
     * @param value
     */
    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        }

        makeAccessible(field);

        Object result = null;

        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            log.error("get field [" + fieldName + "] value on [" + object + "] failed!", e);
        }

        return result;
    }

    /**
     * get method from the specified object(will find all parent)
     * @param object
     * @param methodName
     * @param parameterTypes
     * @return
     */
    private static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
            }
        }

        log.warn("No method matched [" + methodName + "] in " + object.getClass());

        return null;
    }

    /**
     * make the none-accessible field to an accessible field
     * @param field
     */
    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    /**
     * get specified field(will find all parent)
     * @param object
     * @param filedName
     * @return
     */
    private static Field getDeclaredField(Object object, String filedName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(filedName);
            } catch (NoSuchFieldException e) {
            }
        }

        log.warn("No field matched [" + filedName + "] in " + object.getClass());

        return null;
    }

    /**
     * instantiate a new instance of the specified class
     * @Title: newInstance
     * @Description: instantiate a new instance of the specified class
     * @param @param clazz
     * @param @return
     * @return Object
     */
    public static Object newInstance(String clazz) {
        try {
            Class<?> _class = Class.forName(clazz);

            return _class.newInstance();
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }
}

/**
 * define the reflect method type on common java bean
 *
 * @version Revision: 1.00 Date: Sep 9, 2011
 */
enum ReflectType {SET, GET;
}
