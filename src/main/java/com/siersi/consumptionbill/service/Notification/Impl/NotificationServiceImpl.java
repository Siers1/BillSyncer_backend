package com.siersi.consumptionbill.service.Notification.Impl;

import cn.hutool.core.date.DateTime;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.siersi.consumptionbill.entity.Notification;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.NotificationMapper;
import com.siersi.consumptionbill.service.Notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {
    private final NotificationMapper notificationMapper;

    @Override
    public void read(Long invitationId) {
        QueryWrapper queryWrapper = QueryWrapper.create().eq("invitation_id", invitationId);

        Notification notification = notificationMapper.selectOneByQuery(queryWrapper);

        if (notification == null) {
            throw new BusinessException("无效的通知");
        }

        notification.setIsRead(1);
        notification.setReadTime(DateTime.now());
        notificationMapper.update(notification);
    }
}
