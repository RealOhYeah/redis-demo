package com.heima.jedis.util;


public class Assert {
    /**
     * 断言检查给定的对象不为空
     * @param obj
     * @param msg
     */
    public static void notNull(Object obj, String msg){
        if (obj == null) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * 断言检查给定的字符串不为空
     * @param str
     * @param msg
     */
    public static void hasText(String str, String msg){
        if (str == null) {
            throw new RuntimeException(msg);
        }
        if (str.trim().isEmpty()) {
            throw new RuntimeException(msg);
        }
    }
}
