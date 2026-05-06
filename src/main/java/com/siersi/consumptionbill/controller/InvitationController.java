package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.dto.HandleInvitationRequest;
import com.siersi.consumptionbill.dto.InvitationRequest;
import com.siersi.consumptionbill.service.Invitation.InvitationService;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.InvitationVo;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invitation")
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/send")
    public Result<Void> sendInvitation(@RequestBody @Valid InvitationRequest invitationRequest) {
        invitationService.sendInvitation(invitationRequest);
        return Result.success("邀请成功");
    }

    @PostMapping("/handle")
    public Result<Void> handleInvitation(@RequestBody @Valid HandleInvitationRequest handleInvitationRequest, @RequestHeader("Authorization") String authorization) {
        invitationService.handleInvitation(handleInvitationRequest, authorization);
        return Result.success("操作成功");
    }

    @GetMapping("/list")
    public Result<List<InvitationVo>> getList(@RequestHeader("Authorization") String authorization) {
        return Result.success(invitationService.getInvitationList(authorization));
    }
}
