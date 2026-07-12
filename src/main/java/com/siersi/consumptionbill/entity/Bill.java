package com.siersi.consumptionbill.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账单实体类
 * 对应数据库中的bill表，用于存储账单基本信息
 * 一个账单可以包含多个消费记录，并可以被多个用户共享
 * 
 * @author siersi
 * @version 1.0
 */
@Data
@Table(value = "bill")
public class Bill implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String billName;

    private Date createTime;

    private Date updateTime;

    private Integer valid;
}
