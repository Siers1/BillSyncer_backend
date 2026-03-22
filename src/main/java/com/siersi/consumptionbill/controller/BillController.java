package com.siersi.consumptionbill.controller;

import com.mybatisflex.core.paginate.Page;
import com.siersi.consumptionbill.annotation.RateLimit;
import com.siersi.consumptionbill.dto.BillDTO;
import com.siersi.consumptionbill.dto.UpdateBillRequest;
import com.siersi.consumptionbill.service.Bill.BillService;
import com.siersi.consumptionbill.utils.PageParam;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.BillVo;
import com.siersi.consumptionbill.vo.UserVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账单管理控制器
 * 负责处理账单相关的HTTP请求，包括创建账单、添加消费记录、查询账单列表等功能
 * 
 * @author siersi
 * @version 1.0
 */
@RestController
@RequestMapping("/bill")
public class BillController {
    
    /**
     * 账单服务层依赖注入
     */
    @Resource
    private BillService billService;

    /**
     * 创建新账单接口
     * 
     * @param billName 账单名称
     * @param authorization 用户授权令牌，用于身份验证
     * @return 创建结果，成功时返回成功信息
     */
    @PostMapping("/create")
    public Result<Void> createBill(@RequestParam String billName, @RequestHeader("Authorization") String authorization) {
        billService.createBill(billName, authorization);
        return Result.success("创建成功");
    }

    /**
     * 获取用户账单列表接口（分页查询）
     * 
     * @param pageParam 分页参数对象，包含页码、页大小等信息
     * @param authorization 用户授权令牌，用于身份验证
     * @return 包含账单列表的分页结果
     */
    @PostMapping("/list")
    @RateLimit(key = "billList", time = 5, count = 20)
    public Result<Page<BillVo>> getBillList(@RequestBody @Valid PageParam<BillDTO> pageParam, @RequestHeader("Authorization") String authorization) {
        return Result.success(billService.getBills(pageParam, authorization));
    }

    /**
     * 获取用户所有账单列表接口
     * @param authorization 用户授权令牌，用于身份验证
     * @return 包含账单列表的 所有结果
     */
    @GetMapping("/all")
    public Result<List<BillVo>> getAllBills(@RequestHeader("Authorization") String authorization) {
        return Result.success(billService.getAllBills(authorization));
    }

    /**
     * 更新账单信息接口
     * 
     * @param updateBillRequest 包含待更新账单信息的请求对象
     * @return 更新结果，成功时返回成功信息
     */
    @PostMapping("/update")
    public Result<Void> updateBill(@RequestBody @Valid UpdateBillRequest updateBillRequest, @RequestHeader("Authorization") String authorization) {
        billService.updateBill(updateBillRequest, authorization);
        return Result.success("更新成功");
    }

    @PostMapping("/delete")
    public Result<Void> deleteBill(@RequestParam Long billId, @RequestHeader("Authorization") String authorization) {
        billService.deleteBill(billId, authorization);
        return Result.success("删除成功");
    }

    @PostMapping("/users")
    public Result<List<UserVo>> getBillUsers(@RequestParam Long billId) {
        return Result.success(billService.getBillUsers(billId));
    }
    
    @PostMapping("/delete-user")
    public Result<Void> deleteBillUser(@RequestParam Long billId, @RequestParam Long userId, @RequestHeader("Authorization") String authorization) {
        billService.deleteBillUser(billId, userId, authorization);
        return Result.success("删除成功");
    }

    @PostMapping("/add-manager")
    public Result<Void> addManager(@RequestParam Long billId, @RequestParam Long userId, @RequestHeader("Authorization") String authorization) {
        billService.addManager(billId, userId, authorization);
        return Result.success("添加成功");
    }

    @PostMapping("/delete-manager")
    public Result<Void> deleteManager(@RequestParam Long billId, @RequestParam Long userId, @RequestHeader("Authorization") String authorization) {
        billService.deleteManager(billId, userId, authorization);
        return Result.success("移除成功");
    }
}
