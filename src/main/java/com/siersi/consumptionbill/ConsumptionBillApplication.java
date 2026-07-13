package com.siersi.consumptionbill;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.siersi.consumptionbill.mapper")
public class ConsumptionBillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumptionBillApplication.class, args);
    }

}
