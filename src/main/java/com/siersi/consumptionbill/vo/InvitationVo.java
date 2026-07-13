package com.siersi.consumptionbill.vo;

import lombok.Data;

import java.util.Date;

@Data
public class InvitationVo {
    private Long id;
    private String billName;
    private String inviterName;
    private Integer status;
    private Date createTime;
}
