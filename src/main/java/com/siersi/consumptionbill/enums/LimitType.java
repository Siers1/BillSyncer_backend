package com.siersi.consumptionbill.enums;

import lombok.Getter;

@Getter
public enum LimitType {
    /**
     * IP限流
     * <p>
     * 含义：根据客户端IP地址进行限流
     * <p>
     * 适用场景：
     * - 防止单个IP恶意刷接口
     * - 保护公开API接口
     * - 防止爬虫过度抓取
     * <p>
     * 限流Key示例：
     * rate_limit:createBill:192.168.1.100
     * <p>
     * 注意事项：
     * - 需要正确获取真实IP（考虑代理、负载均衡）
     * - 同一局域网用户可能共享公网IP
     * - 移动网络IP可能频繁变化
     */
    IP("IP"),

    /**
     * 用户限流
     * <p>
     * 含义：根据登录用户的ID进行限流
     * <p>
     * 适用场景：
     * - 防止单个用户频繁操作
     * - 限制用户的操作频率
     * - 防止用户误操作或程序错误导致的重复提交
     * <p>
     * 限流Key示例：
     * rate_limit:addRecord:user:12345
     * <p>
     * 注意事项：
     * - 必须是已登录用户（需要从token中获取userId）
     * - 未登录用户会抛出异常或降级为IP限流
     * - 适合对登录用户的操作限流
     */
    USER("USER"),

    /**
     * 全局限流
     * <p>
     * 含义：所有用户共享一个限流配额
     * <p>
     * 适用场景：
     * - 保护系统整体负载
     * - 限制第三方API调用总量
     * - 保护数据库查询压力
     * - 限制昂贵操作的总次数
     * <p>
     * 限流Key示例：
     * rate_limit:getAllBills
     * <p>
     * 注意事项：
     * - 不区分用户，所有请求都计入同一个计数器
     * - 可能影响正常用户体验（一个用户刷接口，其他用户也被限）
     * - 适合保护系统级别的资源
     */
    GLOBAL("GLOBAL");

    private final String type;

    LimitType(String type) {
        this.type = type;
    }
}
