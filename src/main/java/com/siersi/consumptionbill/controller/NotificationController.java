package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.entity.Notification;
import com.siersi.consumptionbill.service.Invitation.InvitationService;
import com.siersi.consumptionbill.service.Notification.NotificationService;
import com.siersi.consumptionbill.utils.Result;
import jakarta.annotation.Resource;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final InvitationService invitationService;
    private final NotificationService notificationService;

    @GetMapping("/unread-count")
    public Result<Long> getUnReadCount(@RequestHeader("Authorization") String authorization) {
        return Result.success(invitationService.getUnReadCount(authorization));
    }

    @GetMapping("/read")
    public Result<Void> read(@RequestParam Long invitationId) {
        notificationService.read(invitationId);
        return Result.success();
    }

}
