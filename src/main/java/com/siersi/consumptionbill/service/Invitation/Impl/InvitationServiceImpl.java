package com.siersi.consumptionbill.service.Invitation.Impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.converter.InvitationConverter;
import com.siersi.consumptionbill.dto.HandleInvitationRequest;
import com.siersi.consumptionbill.dto.InvitationRequest;
import com.siersi.consumptionbill.dto.UserBillDTO;
import com.siersi.consumptionbill.entity.Invitation;
import com.siersi.consumptionbill.entity.Notification;
import com.siersi.consumptionbill.entity.UserBill;
import com.siersi.consumptionbill.enums.InvitationStatus;
import com.siersi.consumptionbill.enums.UserBillRole;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.InvitationMapper;
import com.siersi.consumptionbill.mapper.InvitationVoMapper;
import com.siersi.consumptionbill.mapper.NotificationMapper;
import com.siersi.consumptionbill.mapper.UserBillMapper;
import com.siersi.consumptionbill.service.Bill.BillService;
import com.siersi.consumptionbill.service.Invitation.InvitationService;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.vo.InvitationVo;
import com.siersi.consumptionbill.websocket.WebSocketService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, Invitation> implements InvitationService {
    private final InvitationMapper invitationMapper;
    private final NotificationMapper notificationMapper;
    private final InvitationVoMapper invitationVoMapper;
    private final UserBillMapper userBillMapper;
    private final BillService billService;
    private final UserService userService;

    public boolean checkPermission(InvitationRequest invitationRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("bill_id", invitationRequest.getBillId())
                .eq("user_id", invitationRequest.getInviterId());

        //如果无权限邀请就返回true
        return userBillMapper.selectOneByQuery(queryWrapper).getRoleId().equals(UserBillRole.MEMBER.getRoleId());
    }

    public boolean checkInvited(InvitationRequest invitationRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("bill_id", invitationRequest.getBillId())
                .eq("invitee_id", invitationRequest.getInviteeId())
                .eq("status", InvitationStatus.PENDING.getStatus());

        Invitation invitation = invitationMapper.selectOneByQuery(queryWrapper);

        //如果存在待处理状态的邀请，返回true阻止新邀请
        return invitation != null;
    }

    public boolean checkInBill(InvitationRequest invitationRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("bill_id", invitationRequest.getBillId())
                .eq("user_id", invitationRequest.getInviteeId())
                .eq("valid", 1);

        UserBill userBill = userBillMapper.selectOneByQuery(queryWrapper);

        //如果关联表中存在这个账本和用户关联,返回true
        return (userBill != null);
    }

    public boolean checkBillExists(Long billId) {
        return billService.getById(billId) == null;
    }

    public boolean checkSelfInvitation(InvitationRequest invitationRequest) {
        //如果邀请人和被邀请人是同一个人，返回true
        return invitationRequest.getInviterId().equals(invitationRequest.getInviteeId());
    }

    @Override
    public void sendInvitation(InvitationRequest invitationRequest) {
        if (checkBillExists(invitationRequest.getBillId())) {
            throw new BusinessException("账本不存在");
        }

        if (checkSelfInvitation(invitationRequest)) {
            throw new BusinessException("不能邀请自己");
        }

        if (checkPermission(invitationRequest)) {
            throw new BusinessException("你无权邀请");
        }

        if (checkInvited(invitationRequest)) {
            throw new BusinessException("该用户已被邀请");
        }

        if (checkInBill(invitationRequest)) {
            throw new BusinessException("该用户已经在此账本中");
        }

        Invitation invitation = InvitationConverter.INSTANCE.toInvitation(invitationRequest);

        invitationMapper.insertSelective(invitation);

        Notification notification = new Notification();
        notification.setInvitationId(invitation.getId());
        notification.setUserId(invitation.getInviteeId());
        notificationMapper.insertSelective(notification);

        WebSocketService.sendMessage(invitation.getInviteeId(), "你有新的账本邀请待处理");
    }

    @Override
    public void handleInvitation(HandleInvitationRequest handleInvitationRequest, String authorization) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("id", handleInvitationRequest.getInvitationId())
                .eq("valid", 1);

        Invitation invitation = invitationMapper.selectOneByQuery(queryWrapper);

        if (invitation == null) {
            throw new BusinessException("无效的邀请");
        }

        if (!invitation.getStatus().equals(InvitationStatus.PENDING.getStatus())) {
            throw new BusinessException("邀请已处理");
        }

        if (!invitation.getInviteeId().equals(userService.getIdByAuthorization(authorization))) {
            throw new BusinessException("你无权操作");
        }

        if (handleInvitationRequest.getOptionCode() == 1) {
            invitation.setStatus(InvitationStatus.APPROVED.getStatus());

            UserBillDTO userBillDTO = new UserBillDTO();
            userBillDTO.setBillId(invitation.getBillId());
            userBillDTO.setUserId(invitation.getInviteeId());
            billService.addUserBill(userBillDTO);
        } else if (handleInvitationRequest.getOptionCode() == 2) {
            invitation.setStatus(InvitationStatus.REJECTED.getStatus());
        } else {
            throw new BusinessException("无效的操作");
        }

        invitationMapper.update(invitation);
    }

    @Override
    public Long getUnReadCount(String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("user_id", userId)
                .eq("is_read", 0);

        return notificationMapper.selectCountByQuery(queryWrapper);
    }

    @Override
    public List<InvitationVo> getInvitationList(String authorization) {
        Long userId = userService.getIdByAuthorization(authorization);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .from("invitation")
                .eq("invitee_id", userId)
                .eq("valid", 1);

        return invitationVoMapper.selectListByQuery(queryWrapper);
    }
}
