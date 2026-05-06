package com.siersi.consumptionbill.controller;

import com.mybatisflex.core.paginate.Page;
import com.siersi.consumptionbill.dto.AddRecordRequest;
import com.siersi.consumptionbill.dto.RecordDTO;
import com.siersi.consumptionbill.dto.UpdateRecordRequest;
import com.siersi.consumptionbill.entity.Record;
import com.siersi.consumptionbill.service.Record.RecordService;
import com.siersi.consumptionbill.utils.PageParam;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.RecordVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消费记录管理控制器
 * 负责处理消费记录相关的HTTP请求，如查询消费记录列表等
 * 
 * @author siersi
 * @version 1.0
 */
@RestController
@RequestMapping("/record")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;

    /**
     * 获取消费记录列表接口（分页查询）
     * 
     * @param pageParam 分页参数对象，包含页码、页大小等信息
     * @return 包含消费记录列表的分页结果
     */
    @PostMapping("/list")
    public Result<Page<RecordVo>> getRecordList(@RequestBody @Valid PageParam<RecordDTO> pageParam) {
        return Result.success(recordService.getRecords(pageParam));
    }

    /**
     * 向账单添加消费记录接口
     *
     * @param addRecordRequest 包含消费记录详细信息的请求对象
     * @return 添加结果，成功时返回成功信息
     */
    @PostMapping("/add-record")
    public Result<Void> addRecordToBill(@RequestBody @Valid AddRecordRequest addRecordRequest) {
        recordService.addRecordToBill(addRecordRequest);
        return Result.success("添加成功");
    }

    @PostMapping("/update")
    public Result<Void> updateRecord(@RequestBody @Valid UpdateRecordRequest updateRecordRequest) {
        recordService.updateRecord(updateRecordRequest);
        return Result.success("更新成功");
    }
}
