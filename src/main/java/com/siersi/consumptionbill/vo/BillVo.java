package com.siersi.consumptionbill.vo;

import lombok.Data;

import java.util.Date;

/**
 * 账单视图对象
 * 用于向前端返回账单基本信息
 * 主要用于账单列表展示
 * 
 * @author siersi
 * @version 1.0
 */
@Data
public class BillVo {

    private Long id;
    private String billName;
    private Date createTime;
}
