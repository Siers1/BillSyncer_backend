package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.service.Budget.BudgetService;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.BudgetValueVo;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/set")
    public Result<Void> setBillBudget(@RequestParam Long billId,
                                      @RequestParam BigDecimal value,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
                                      @RequestHeader("Authorization") String authorization) {
        budgetService.setBillBudget(billId, value, dateTime, authorization);
        return Result.success("设置成功");
    }

    @GetMapping("/clear")
    public Result<Void> clearBillBudget(@RequestParam Long billId,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
                                        @RequestHeader("Authorization") String authorization) {
        budgetService.clearBillBudget(billId, dateTime, authorization);
        return Result.success("清除成功");
    }

    @GetMapping("/get")
    public Result<BudgetValueVo> getBillBudgetValue(@RequestParam Long billId,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dateTime,
                                                    @RequestHeader("Authorization") String authorization) {
        return Result.success(budgetService.getBillBudgetValue(billId, dateTime, authorization));
    }
}
