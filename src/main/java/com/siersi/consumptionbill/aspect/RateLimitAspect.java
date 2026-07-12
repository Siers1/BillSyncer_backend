package com.siersi.consumptionbill.aspect;

import com.siersi.consumptionbill.annotation.RateLimit;
import com.siersi.consumptionbill.exception.RateLimitException;
import com.siersi.consumptionbill.service.User.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RedissonClient redissonClient;
    private final HttpServletRequest request;
    private final UserService userService;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        try {
            String limitKey = buildLimitKey(joinPoint, rateLimit);
            log.debug("限流Key: {}", limitKey);

            RRateLimiter rateLimiter = redissonClient.getRateLimiter(limitKey);

            boolean rateSet = rateLimiter.trySetRate(
                    RateType.OVERALL, // 限流模式: OVERALL表示全局限流
                    rateLimit.count(), // 令牌数量: 限制时间内允许的请求次数
                    rateLimit.time(), // 限制时间
                    RateIntervalUnit.SECONDS // 秒为单位
            );

            if (rateSet) {
                log.info("初始化限流器: {}, 规则: {}次/{} 秒", limitKey, rateLimit.count(), rateLimit.time());
            }

            boolean acquire = rateLimiter.tryAcquire();

            if (!acquire) {
                log.warn("接口被限流 - key: {}, 规则: {}次/{}秒", limitKey, rateLimit.count(), rateLimit.time());

                throw new RateLimitException("访问过于频繁，请稍后再试");
            }

            return joinPoint.proceed();
        } catch (RateLimitException e) {
            throw e;
        } catch (Exception e) {
            log.error("限流组件异常，降级放行请求 - 方法: {}, 异常: {}", joinPoint.getSignature().getName(), e.getMessage(), e);

            return joinPoint.proceed();
        }
    }

    /**
     * 构造限流key
     *
     * 根据限流类型，生成不同的Redis key：
     * - IP限流：rate_limit:方法名:IP地址
     * - USER限流：rate_limit:方法名:user:用户ID
     * - GLOBAL限流：rate_limit:方法名
     *
     * @param joinPoint 连接点
     * @param rateLimit 限流注解
     * @return Redis限流key
     */
    private String buildLimitKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        String key = rateLimit.key();

        if (key == null || key.isEmpty()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            key = signature.getMethod().getName();
        }

        String limitKey = switch (rateLimit.limitType()) {
            case IP -> {
                String ip = getIpAddress(request);
                yield "rate_limit: " + key + ": " + ip;
            }

            case USER -> {
                String token = request.getHeader("Authorization");

                if (token == null || token.isEmpty()) {
                    throw new RateLimitException("用户未登录");
                }

                Long userId = userService.getIdByAuthorization(token);

                yield "rate_limit: " + key + ": user: " + userId;
            }

            case GLOBAL -> {
                yield "rate_limit: " + key;
            }
        };

        return limitKey;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (isValidIp(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index).trim();
            }
            return ip.trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }

        ip = request.getRemoteAddr();

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip != null ? ip.trim() : "unknown";
    }

    private boolean isValidIp(String ip) {
        return ip != null
                && !ip.isEmpty()
                && !"unknown".equalsIgnoreCase(ip);
    }
}
