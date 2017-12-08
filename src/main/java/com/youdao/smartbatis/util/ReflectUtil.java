package com.youdao.smartbatis.util;

import java.lang.reflect.Method;

/**
 * @author liugang
 */
public class ReflectUtil {
    /**
     * 根据方法名查找方法，如果当前类有函数重载，返回找到的第一个方法。
     * @param clazz
     * @param name
     * @return
     */
    public static Method getMethodByName(Class clazz, String name){
        if (clazz == null)
            return null;
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            if (method.getName().equals(name))
                return method;
        }
        return null;
    }

    public static Class getClass(String fullName){
        Class clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
}
