package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.dto.StatisticDTO;
import com.siersi.consumptionbill.service.Statistics.StatisticsService;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.StatisticsVo;
import com.siersi.consumptionbill.vo.PaymentVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final UserService userService;

    @PostMapping("/summary")
    public Result<List<StatisticsVo>> getStatistics(@RequestBody @Valid StatisticDTO statisticDTO) {
        return Result.success(statisticsService.getStatistics(statisticDTO));
    }

    @GetMapping("/type")
    public Result<List<PaymentVo>> getTypeSt(@RequestHeader("Authorization") String authorization) {
        return Result.success(statisticsService.getTypeSt(userService.getIdByAuthorization(authorization)));
    }
}
