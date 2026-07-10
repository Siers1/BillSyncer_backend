package com.siersi.consumptionbill.annotation;

import com.siersi.consumptionbill.enums.LimitType;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流key的名称
     * 用于区分不同的接口
     * 如果不指定, 默认使用方法名
     */
    String key() default "";

    /**
     * 表示在多长时间内进行限流统计(单位: 秒)
     * 默认 60 秒
     */
    int time() default 60;

    /**
     * 设定时间内允许的最大请求次数
     * 超过次数就会被限流
     * 默认 50 次
     */
    int count() default 50;

    /**
     * 限流类型
     * 决定按什么维度进行限流
     * 可选值：
     * - LimitType.IP：根据IP地址限流（防止单个IP刷接口）
     * - LimitType.USER：根据用户ID限流（防止单个用户频繁操作）
     * - LimitType.GLOBAL：全局限流（所有用户共享配额）
     */
    LimitType limitType() default LimitType.IP;
}
