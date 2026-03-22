package com.siersi.consumptionbill.service.Record.Impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.converter.RecordConverter;
import com.siersi.consumptionbill.dto.AddRecordRequest;
import com.siersi.consumptionbill.dto.RecordDTO;
import com.siersi.consumptionbill.dto.UpdateRecordRequest;
import com.siersi.consumptionbill.entity.Record;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.RecordMapper;
import com.siersi.consumptionbill.mapper.RecordVoMapper;
import com.siersi.consumptionbill.service.Record.RecordService;
import com.siersi.consumptionbill.utils.PageParam;
import com.siersi.consumptionbill.vo.RecordVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消费记录服务实现类
 * 实现消费记录相关的业务逻辑，主要提供按账单ID查询消费记录的功能
 * 
 * @author siersi
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Resource
    private RecordVoMapper recordVoMapper;

    @Resource
    private RecordMapper recordMapper;

    /**
     * 获取消费记录列表实现（分页查询）
     * 根据账单ID查询该账单下的所有有效消费记录
     * 
     * @param pageParam 分页参数对象，其中params字段包含账单ID
     * @return 消费记录列表的分页结果
     * @throws BusinessException 当账单ID为空时抛出异常
     */
    @Override
    public Page<RecordVo> getRecords(PageParam<RecordDTO> pageParam) {
        if(pageParam.getParams().getBillId() == null){
            throw new BusinessException(400, "账本不能为空");
        }

        QueryWrapper queryWrapper = QueryWrapper.create()
                .from("record")
                .where("bill_id = ?", pageParam.getParams().getBillId())
                .and("valid = 1");

        if (StrUtil.isNotBlank(pageParam.getParams().getKeyWords())) {
            String keyWords = "%" + pageParam.getParams().getKeyWords() + "%";
            queryWrapper.and(w -> {
                w.where("item_name LIKE ?", keyWords)
                        .or("item_price LIKE ?", keyWords)
                        .or("consumption_type LIKE ?", keyWords)
                        .or("payment_method LIKE ?", keyWords)
                        .or("comment LIKE ?", keyWords);
            });
        }

//        System.out.println(recordMapper.selectListByQuery(queryWrapper).toString());
        // 执行分页查询并返回结果
        return recordVoMapper.paginate(Page.of(pageParam.getPageNum(),pageParam.getPageSize()), queryWrapper);
    }

    /**
     * 向账单添加消费记录实现
     * 根据请求信息创建新的消费记录并保存到数据库
     *
     * @param addRecordRequest 添加记录请求对象，包含消费记录的详细信息
     */
    @Override
    public void addRecordToBill(AddRecordRequest addRecordRequest) {
        // 创建消费记录对象并设置相关信息
        Record record = RecordConverter.INSTANCE.ToRecord(addRecordRequest);

        // 保存消费记录到数据库
        recordMapper.insertSelective(record);
    }

    @Override
    public void updateRecord(UpdateRecordRequest updateRecordRequest) {
        Record record = RecordConverter.INSTANCE.ToRecord(updateRecordRequest);
        recordMapper.update(record);
    }
}
