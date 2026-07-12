package com.siersi.consumptionbill.service.Budget;

import com.mybatisflex.core.service.IService;
import com.siersi.consumptionbill.entity.Budget;
import com.siersi.consumptionbill.vo.BudgetValueVo;

import java.math.BigDecimal;
import java.util.Date;

public interface BudgetService extends IService<Budget> {
    void setBillBudget(Long billId, BigDecimal value, Date dateTime, String authorization);
    void clearBillBudget(Long billId, Date dateTime, String authorization);
    BudgetValueVo getBillBudgetValue(Long billId, Date dateTime, String authorization);
}
