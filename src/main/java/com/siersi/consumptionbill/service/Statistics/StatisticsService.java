package com.siersi.consumptionbill.service.Statistics;

import com.mybatisflex.core.service.IService;
import com.siersi.consumptionbill.dto.StatisticDTO;
import com.siersi.consumptionbill.vo.PaymentVo;
import com.siersi.consumptionbill.vo.StatisticsVo;

import java.util.List;

public interface StatisticsService extends IService<StatisticsVo> {
    List<StatisticsVo> getStatistics(StatisticDTO statisticDTO, String authorization);

    List<PaymentVo> getTypeSt(Long userId);
}
