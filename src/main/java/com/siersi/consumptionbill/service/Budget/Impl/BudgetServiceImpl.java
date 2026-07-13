package com.siersi.consumptionbill.service.Budget.Impl;

import cn.hutool.core.date.DateUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.entity.Budget;
import com.siersi.consumptionbill.entity.UserBill;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.BudgetMapper;
import com.siersi.consumptionbill.mapper.UserBillMapper;
import com.siersi.consumptionbill.service.Budget.BudgetService;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.vo.BudgetValueVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private final BudgetMapper budgetMapper;
    private final UserService userService;
    private final UserBillMapper userBillMapper;

    @Override
    public void setBillBudget(Long billId, BigDecimal value, Date dateTime, String authorization) {
        check(billId, authorization);

        QueryWrapper qw = QueryWrapper.create()
                .eq("bill_id", billId)
                .eq("date_time", dateTime)
                .eq("valid", 1);

        Budget budget = budgetMapper.selectOneByQuery(qw);

        if (budget == null) {
            budget = new Budget();
            budget.setBillId(billId);
            budget.setValue(value);
            budget.setDateTime(dateTime);
            budgetMapper.insertSelective(budget);
        } else {
            budget.setValue(value);
            budgetMapper.update(budget);
        }

    }

    @Override
    public void clearBillBudget(Long billId, Date dateTime, String authorization) {
        check(billId, authorization);

        QueryWrapper qw = QueryWrapper.create()
                .eq("bill_id", billId)
                .eq("date_time", dateTime)
                .eq("valid", 1);

        Budget budget = budgetMapper.selectOneByQuery(qw);

        budget.setValue(null);
        budgetMapper.update(budget,false);
    }

    @Override
    public BudgetValueVo getBillBudgetValue(Long billId, Date dateTime, String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);
        QueryWrapper qw1 = QueryWrapper.create()
                .eq("bill_id", billId)
                .eq("user_id", userId)
                .eq("valid", 1);

        UserBill userBill = userBillMapper.selectOneByQuery(qw1);

        if (userBill == null) {
            throw new BusinessException(403, "无操作权限");
        }

        QueryWrapper qw2 = QueryWrapper.create()
                .eq("bill_id", billId)
                .eq("date_time", dateTime)
                .eq("valid", 1);

        Budget budget = budgetMapper.selectOneByQuery(qw2);

        QueryWrapper qw3 = QueryWrapper.create()
                .select("SUM(item_price) AS expenses")
                .from("record")
                .eq("bill_id", billId)
                .ge("create_time", dateTime)
                .lt("create_time", DateUtil.offsetMonth(dateTime, 1))
                .eq("valid", 1);

        Row resultMap = Db.selectOneByQuery(qw3);

        BudgetValueVo budgetValueVo = new BudgetValueVo();
        budgetValueVo.setBudget(budget != null ? budget.getValue() : null);
        budgetValueVo.setExpenses(resultMap != null ? (BigDecimal) resultMap.get("expenses") : BigDecimal.ZERO);

        return budgetValueVo;
    }

    private void check(Long billId, String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);

        QueryWrapper qw = QueryWrapper.create()
                .eq("bill_id", billId)
                .eq("valid", 1);

        UserBill userBIll = userBillMapper.selectOneByQuery(qw);

        if (!userBIll.getUserId().equals(userId)) {
            throw new BusinessException(403, "无操作权限");
        }
    }
}
