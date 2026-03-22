package com.siersi.consumptionbill.service.Bill;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.siersi.consumptionbill.dto.BillDTO;
import com.siersi.consumptionbill.dto.UpdateBillRequest;
import com.siersi.consumptionbill.dto.UserBillDTO;
import com.siersi.consumptionbill.entity.Bill;
import com.siersi.consumptionbill.utils.PageParam;
import com.siersi.consumptionbill.vo.BillVo;
import com.siersi.consumptionbill.vo.UserVo;

import java.util.List;

/**
 * 账单服务接口
 * 定义账单相关的业务操作，包括创建账单、添加消费记录、查询账单列表、更新账单等功能
 * 
 * @author siersi
 * @version 1.0
 */
public interface BillService extends IService<Bill> { 
    
    /**
     * 创建新账单
     * 
     * @param billName 账单名称
     * @param authorization 用户授权令牌
     */
    void createBill(String billName, String authorization);

    void addUserBill(UserBillDTO userBillDTO);

    /**
     * 获取用户的账单列表（分页查询）
     * 
     * @param pageParam 分页参数对象
     * @param authorization 用户授权令牌
     * @return 账单列表的分页结果
     */
    Page<BillVo> getBills(PageParam<BillDTO> pageParam, String authorization);

    /**
     * 更新账单信息
     * 
     * @param updateBillRequest 账单更新请求对象，包含待更新的账单信息
     */
    void updateBill(UpdateBillRequest updateBillRequest, String authorization);

    void deleteBill(Long billId, String authorization);

    List<BillVo> getAllBills(String authorization);

    List<UserVo> getBillUsers(Long billId);

    void deleteBillUser(Long billId, Long userId, String authorization);

    void addManager(Long billId, Long userId, String authorization);

    void deleteManager(Long billId, Long userId, String authorization);
}
