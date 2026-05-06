package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.dto.LoginRequest;
import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.JwtUtil;
import com.siersi.consumptionbill.utils.Result;
import com.siersi.consumptionbill.websocket.WebSocketService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证控制器
 * 负责处理用户注册、登录等认证相关的HTTP请求
 * 
 * @author siersi
 * @version 1.0
 */
@RestController()
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册接口
     * 
     * @param loginRequest 包含用户账号和密码的注册请求对象
     * @return 注册结果，成功时返回成功信息
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid LoginRequest loginRequest) {
        userService.register(loginRequest);
        return Result.success("注册成功");
    }

    /**
     * 用户登录接口
     * 
     * @param loginRequest 包含用户账号和密码的登录请求对象
     * @return 登录结果，成功时返回双token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginRequest loginRequest) {
        userService.login(loginRequest);

        if (jwtUtil.hasActiveLogin(loginRequest.getAccount())) {
            Long originalUserId = userService.getIdByAccount(loginRequest.getAccount());
            WebSocketService.sendMessage(originalUserId, "您的账号在其他设备登录，即将自动退出");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Map<String, Object> tokens = jwtUtil.generateTokensWithForceLogout(loginRequest.getAccount());
        return Result.success("登录成功", tokens);
    }

    /**
     * 刷新token接口
     * 
     * @param request 包含refreshToken的请求
     * @return 刷新结果，成功时返回新的双token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, Object> newTokens = jwtUtil.refreshToken(refreshToken);
        return Result.success("刷新成功", newTokens);
    }

    /**
     * 退出接口
     * 
     * @param request HTTP请求对象
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            String account = jwtUtil.getAccountFromToken(token);
            jwtUtil.logout(account);
        }
        return Result.success("退出成功");
    }
}
