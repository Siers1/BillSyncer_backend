package com.siersi.consumptionbill.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetValueVo {
    private BigDecimal budget;
    private BigDecimal expenses;
}
