package com.siersi.consumptionbill.config;

import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT身份验证拦截器
 * 在请求处理前拦截并验证用户的JWT令牌，确保只有已认证的用户才能访问受保护的接口
 * 
 * @author siersi
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    /**
     * 在请求处理前执行的方法，用于验证JWT令牌
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @return 如果验证通过返回true，允许继续处理请求；如果验证失败抛出异常
     * @throws Exception 当JWT令牌无效或不存在时抛出业务异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // 从请求头中获取Authorization字段
        final String authorization = request.getHeader("Authorization");

        // 检查Authorization头是否存在且格式正确
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(401, "请登录");
        }

        // 提取JWT令牌（去掉"Bearer "前缀）
        final String token = authorization.substring(7);

        // 验证JWT令牌的有效性
        if (!jwtUtil.verifyAccessToken(token)) {
            throw new BusinessException(401, "访问令牌无效或已过期");
        }

        // 验证通过，允许继续处理请求
        return true;
    }
}
