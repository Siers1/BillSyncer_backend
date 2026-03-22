package com.siersi.consumptionbill.service.Bill.Impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.converter.BillConverter;
import com.siersi.consumptionbill.converter.UserBillConverter;
import com.siersi.consumptionbill.dto.BillDTO;
import com.siersi.consumptionbill.dto.UpdateBillRequest;
import com.siersi.consumptionbill.dto.UserBillDTO;
import com.siersi.consumptionbill.entity.Bill;
import com.siersi.consumptionbill.entity.UserBill;
import com.siersi.consumptionbill.enums.UserBillRole;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.*;
import com.siersi.consumptionbill.service.Bill.BillService;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.PageParam;
import com.siersi.consumptionbill.vo.BillVo;
import com.siersi.consumptionbill.vo.UserVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账单服务实现类
 * 实现账单相关的业务逻辑，包括创建账单、添加消费记录、查询账单列表、更新账单等功能
 * 支持多用户共享账单功能，通过UserBill关联表管理用户与账单的关系
 * 
 * @author siersi
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillServiceImpl extends ServiceImpl<BillMapper, Bill> implements BillService {
    
    /**
     * 账单数据访问层接口
     */
    @Resource
    private BillMapper billMapper;

    /**
     * 账单视图对象数据访问层接口
     */
    @Resource
    private BillVoMapper billVoMapper;

    /**
     * 用户账单关联数据访问层接口
     */
    @Resource
    private UserBillMapper userBillMapper;

    /**
     * 用户服务接口，用于获取用户信息
     */
    @Resource
    private UserService userService;

    @Resource
    private UserVoMapper userVoMapper;

    /**
     * 创建新账单实现
     * 创建账单并建立用户与账单的关联关系，设置创建者为账单管理员
     * 
     * @param billName 账单名称
     * @param authorization 用户授权令牌
     */
    @Override
    public void createBill(String billName, String authorization) {
        // 创建新账单
        Bill bill = new Bill();
        bill.setBillName(billName);
        billMapper.insertSelective(bill);

        // 建立用户与账单的关联关系
        UserBill userBill = new UserBill();
        userBill.setBillId(bill.getId());
        userBill.setUserId(userService.getIdByAuthorization(authorization));
        userBill.setRoleId(UserBillRole.CREATOR.getRoleId());  // 设置为创建者角色
        userBillMapper.insertSelective(userBill);
    }

    /**
     * 新增账本用户
     * @param userBillDTO 用户账本关联DTO
     */
    @Override
    public void addUserBill(UserBillDTO userBillDTO) {
        UserBill userBill = UserBillConverter.INSTANCE.ToUserBill(userBillDTO);
        userBill.setRoleId(UserBillRole.MEMBER.getRoleId());
        userBillMapper.insertSelective(userBill);
    }

    /**
     * 获取用户账单列表实现（分页查询）
     * 通过用户ID查询该用户有权访问的所有账单，支持分页
     * 
     * @param pageParam 分页参数对象
     * @param authorization 用户授权令牌
     * @return 账单列表的分页结果
     */
    @Override
    public Page<BillVo> getBills(PageParam<BillDTO> pageParam, String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("bill.*")
                .from("bill")
                .innerJoin("user_bill").on("bill.id = user_bill.bill_id")
                .where("user_bill.user_id = ?", userId)
                .and("bill.valid = 1")
                .and("user_bill.valid = 1");

        if (StrUtil.isNotBlank(pageParam.getParams().getKeyWords())) {
            String keyWords = "%" + pageParam.getParams().getKeyWords() + "%";
            queryWrapper.and(w -> {
                w.where("bill_name LIKE ?", keyWords);
            });
        }

        return billVoMapper.paginate(Page.of(pageParam.getPageNum(),pageParam.getPageSize()), queryWrapper);
    }

    /**
     * 获取用户账单列表实现
     * @param authorization 用户授权令牌
     * @return 账单列表的所有结果
     */
    @Override
    public List<BillVo> getAllBills(String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);

        // 构建查询条件：查询用户有权访问的有效账单
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("bill.*")
                .from("bill")
                .innerJoin("user_bill").on("bill.id = user_bill.bill_id")
                .where("user_bill.user_id = ?", userId)
                .and("bill.valid = 1 and user_bill.valid = 1");

        return billVoMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public List<UserVo> getBillUsers(Long billId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select("user.*", "user_bill.role_id")
                .from("user")
                .innerJoin("user_bill").on("user.id = user_bill.user_id")
                .where("user_bill.bill_id = ?", billId)
                .and("user_bill.valid = 1");

        return userVoMapper.selectListByQuery(queryWrapper);
    }

    @Override
    public void deleteBillUser(Long billId, Long userId, String authorization) {
        if (billId == null || userId == null) {
            throw new BusinessException("账本和用户不能为空");
        }

        Long opUserId = userService.getIdByAuthorization(authorization);
        UserBill opUserBill = userBillMapper.selectOneByQuery(QueryWrapper.create()
                .eq("user_id", opUserId)
                .eq("bill_id", billId)
                .eq("valid", 1));

        if (opUserBill == null) {
            throw new BusinessException("账本无效或你不在此帐本中");
        }

        UserBill targetUserBill = userBillMapper.selectOneByQuery(QueryWrapper.create()
                .from("user_bill")
                .eq("user_id", userId)
                .eq("bill_id", billId)
                .eq("valid", 1));

        if (targetUserBill == null) {
            throw new BusinessException("用户不在帐本中");
        }

        //同等级之间不能相互删除, 创建者可以删除管理员, 管理员可以删除成员
        if (opUserBill.getRoleId() >= targetUserBill.getRoleId()) {
            throw new BusinessException("你的权限不足");
        }

        targetUserBill.setValid(0);
        userBillMapper.update(targetUserBill);
    }

    /**
     * 更新账单信息实现
     * 根据账单ID查找账单并更新其信息
     * 
     * @param updateBillRequest 账单更新请求对象，包含待更新的账单信息
     * @throws BusinessException 当账单不存在时抛出异常
     */
    @Override
    public void updateBill(UpdateBillRequest updateBillRequest, String authorization) {
        // 验证操作者权限(需要是创建者)
        checkPermission(updateBillRequest.getBillId(), authorization, UserBillRole.CREATOR);

        // 检查账单是否存在
        Bill bill = billMapper.selectOneById(updateBillRequest.getBillId());

        if (bill == null) {
            throw new BusinessException(400, "账本不存在");
        }

        // 使用转换器将请求对象转换为实体对象
        bill = BillConverter.INSTANCE.ToBill(updateBillRequest);

        // 更新账单信息
        billMapper.update(bill);
    }

    @Override
    public void deleteBill(Long billId, String authorization) {
        // 验证操作者权限(需要是创建者)
        checkPermission(billId, authorization, UserBillRole.CREATOR);

        Bill bill = billMapper.selectOneById(billId);
        bill.setValid(0);
        billMapper.update(bill);
    }

    @Override
    public void addManager(Long billId, Long userId, String authorization) {
        // 验证操作者权限(需要是创建者)
        checkPermission(billId, authorization, UserBillRole.CREATOR);

        // 更新目标用户角色
        UserBill userBill = userBillMapper.selectOneByQuery(QueryWrapper.create()
                .eq("user_id", userId).eq("bill_id", billId).eq("valid", 1));

        if (userBill == null) {
            throw new BusinessException("账本或用户不存在");
        }
        if (!userBill.getRoleId().equals(UserBillRole.MEMBER.getRoleId())) {
            throw new BusinessException("用户已是管理员");
        }

        userBill.setRoleId(UserBillRole.MANAGER.getRoleId());
        userBillMapper.update(userBill);
    }

    @Override
    public void deleteManager(Long billId, Long userId, String authorization) {
        // 验证操作者权限(需要是创建者)
        checkPermission(billId, authorization, UserBillRole.CREATOR);

        // 更新目标用户角色
        UserBill userBill = userBillMapper.selectOneByQuery(QueryWrapper.create()
                .eq("user_id", userId).eq("bill_id", billId).eq("valid", 1));

        if (userBill == null) {
            throw new BusinessException("账本或用户不存在");
        }
        if (userBill.getRoleId().equals(UserBillRole.MEMBER.getRoleId())) {
            throw new BusinessException("用户已是成员");
        }

        userBill.setRoleId(UserBillRole.MEMBER.getRoleId());
        userBillMapper.update(userBill);
    }

    /**
     * 检查用户对账本的权限
     *
     * @param billId        账本ID
     * @param authorization 用户授权令牌
     * @param requiredRole  需要的角色(如果为null则只检查用户是否在账本中)
     * @throws BusinessException 当用户权限不足或不在账本中时抛出异常
     */
    private void checkPermission(Long billId, String authorization, UserBillRole requiredRole) {
        Long opUserId = userService.getIdByAuthorization(authorization);

        // 查询操作者在账本中的权限
        UserBill opUserBill = userBillMapper.selectOneByQuery(QueryWrapper.create()
                .eq("user_id", opUserId)
                .eq("bill_id", billId)
                .eq("valid", 1));

        if (opUserBill == null) {
            throw new BusinessException("账本无效或你不在此账本中");
        }

        // 如果指定了需要的角色,则验证权限
        if (requiredRole != null && !opUserBill.getRoleId().equals(requiredRole.getRoleId())) {
            throw new BusinessException("你的权限不足");
        }
    }
}
