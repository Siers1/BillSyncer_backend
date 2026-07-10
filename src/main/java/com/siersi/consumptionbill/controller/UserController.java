package com.siersi.consumptionbill.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.siersi.consumptionbill.dto.PasswordDTO;
import com.siersi.consumptionbill.dto.UserDTO;
import com.siersi.consumptionbill.mapper.UserVoMapper;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户信息管理控制器
 * 负责处理用户信息相关的HTTP请求，如获取用户详情等
 * 
 * @author siersi
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserVoMapper userVoMapper;

    /**
     * 获取当前登录用户详细信息接口
     * 
     * @param authorization 用户授权令牌，通过请求头传递
     * @return 包含用户详细信息的响应结果
     */
    @GetMapping("/get-details")
    public Result<UserVo> getUserDetails(@RequestHeader("Authorization") String authorization) {
        return Result.success(userService.getUserById(userService.getIdByAuthorization(authorization)));
    }

    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return Result.success("更新成功");
    }

    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody PasswordDTO passwordDTO, @RequestHeader("Authorization") String authorization) {
        userService.changePassword(passwordDTO, authorization);
        return Result.success("更改成功, 请重新登录");
    }

    @PostMapping("/search")
    public Result<List<UserVo>> searchUser(@RequestParam String account) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .from("user")
                .like("account", account);

        List<UserVo> userVoList = userVoMapper.selectListByQuery(queryWrapper);

        return Result.success("请求成功", userVoList);
    }
}
