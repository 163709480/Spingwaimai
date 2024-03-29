package com.itheima.reggie.common;

import org.apache.ibatis.annotations.Lang;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal= new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */

    public  static  void setCruuentID(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCruuentId(){
        return threadLocal.get();
    }



}
