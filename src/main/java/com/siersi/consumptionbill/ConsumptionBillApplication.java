package com.siersi.consumptionbill;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消费账单管理系统启动类
 * 基于Spring Boot框架，提供用户注册登录、账单管理、消费记录管理等功能
 *
 * @author siersi
 * @version 1.0
 */
@SpringBootApplication
@MapperScan("com.siersi.consumptionbill.mapper")
public class ConsumptionBillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumptionBillApplication.class, args);
    }

}
