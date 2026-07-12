package com.siersi.consumptionbill.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 对应数据库中的user表，用于存储用户基本信息
 * 
 * @author siersi
 * @version 1.0
 */
@Data
@Table(value = "user")
public class User implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String account;

    private String password;

    private String username;

    private String avatar;

    private Date createTime;

    private Date updateTime;

    private Integer valid;
}
