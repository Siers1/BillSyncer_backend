package com.siersi.consumptionbill.service.Statistics.Impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.dto.StatisticDTO;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.BillMapper;
import com.siersi.consumptionbill.mapper.StatisticsVoMapper;
import com.siersi.consumptionbill.mapper.TypeMapper;
import com.siersi.consumptionbill.mapper.UserMapper;
import com.siersi.consumptionbill.service.Statistics.StatisticsService;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.vo.PaymentVo;
import com.siersi.consumptionbill.vo.StatisticsVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class StatisticsServiceImpl extends ServiceImpl<StatisticsVoMapper,StatisticsVo> implements StatisticsService {
    private final BillMapper billMapper;
    private final StatisticsVoMapper statisticsVoMapper;
    private final UserMapper userMapper;
    private final TypeMapper typeMapper;
    private final UserService userService;

    @Override
    public List<StatisticsVo> getStatistics(StatisticDTO statisticDTO, String authorization) {

        Long userId = userService.getIdByAuthorization(authorization);

        if (billMapper.selectOneByQuery(QueryWrapper.create()
                .eq("id", statisticDTO.getBillId())
                .eq("valid", 1)) == null) {
            throw new BusinessException("账本不存在");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("DATE(consumption_date) as time, SUM(item_price) as total")
                .from("record")
                .between("consumption_date", statisticDTO.getStartDate(), statisticDTO.getEndDate())
                .eq("creator_id", userId)
                .eq("bill_id", statisticDTO.getBillId())
                .eq("valid", 1)
                .groupBy("DATE(consumption_date)");

        return statisticsVoMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public List<PaymentVo> getTypeSt(Long userId) {

        if (userMapper.selectOneByQuery(QueryWrapper.create()
                .eq("id", userId)) == null) {
            throw new BusinessException("用户不存在");
        }

        long totalCount = Db.selectCountByQuery("record",
                QueryWrapper.create()
                        .eq("creator_id", userId)
                        .eq("valid", 1)
        );

        if (totalCount == 0) {
            return new ArrayList<>();
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("payment_method as type")
                .select("ROUND(COUNT(*) * 100.0 / " + totalCount + ", 2) as proportion")
                .from("record")
                .eq("creator_id", userId)
                .eq("valid", 1)
                .groupBy("payment_method")
                .orderBy("proportion", false);

        return typeMapper.selectListByQuery(queryWrapper);
    }

}
