package com.siersi.consumptionbill.utils;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 用于封装API接口的统一响应格式，包含状态码、消息和数据
 * 
 * @param <T> 响应数据的泛型类型
 * @author siersi
 * @version 1.0
 */
@Data
public class Result<T> {
    
    /**
     * 响应状态码
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造方法，用于创建Result实例
     * 
     * @param code 状态码
     * @param msg 消息
     * @param data 数据
     * @return Result实例
     */
    private static <T> Result<T> of(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 创建成功响应（无数据）
     * 
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return of(200, "请求成功", null);
    }

    /**
     * 创建成功响应（带数据）
     * 
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        return of(200, "请求成功", data);
    }

    /**
     * 创建成功响应（自定义消息）
     * 
     * @param msg 自定义消息
     * @return 成功响应结果
     */
    public static <T> Result<T> success(String msg) {
        return of(200, msg, null);
    }

    /**
     * 创建成功响应（自定义消息和数据）
     * 
     * @param msg 自定义消息
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> Result<T> success(String msg, T data) {
        return of(200, msg, data);
    }

    /**
     * 创建失败响应（默认消息）
     * 
     * @return 失败响应结果
     */
    public static <T> Result<T> failure() {
        return of(500, "请求失败", null);
    }

    /**
     * 创建失败响应（自定义消息）
     * 
     * @param msg 自定义错误消息
     * @return 失败响应结果
     */
    public static <T> Result<T> failure(String msg) {
        return of(400, msg, null);
    }

    /**
     * 创建失败响应（自定义状态码和消息）
     * 
     * @param code 自定义状态码
     * @param msg 自定义错误消息
     * @return 失败响应结果
     */
    public static <T> Result<T> failure(int code, String msg) {
        return of(code, msg, null);
    }
}
