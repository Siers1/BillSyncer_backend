package com.siersi.consumptionbill.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Table("budget")
public class Budget {
    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long billId;

    private BigDecimal value;

    private Date dateTime;

    private Integer valid;
}
