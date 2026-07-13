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
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

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

            String counterKey = "rate_limit_counter:" + limitKey;
            RAtomicLong counter = redissonClient.getAtomicLong(counterKey);

            long currentCount = counter.incrementAndGet();

            // 如果是第一次，设置过期时间
            if (currentCount == 1) {
                counter.expire(Duration.ofSeconds(rateLimit.time()));
                log.info("初始化限流计数器: {}, 规则: {}次/{}秒", limitKey, rateLimit.count(), rateLimit.time());
            }

            if (currentCount > rateLimit.count()) {
                long remainingTime = counter.remainTimeToLive() / 1000;
                if (remainingTime < 0) {
                    remainingTime = rateLimit.time();
                }
                log.warn("接口被限流 - key: {}, 当前: {}次, 规则: {}次/{}秒, 剩余: {}秒",
                        limitKey, currentCount, rateLimit.count(), rateLimit.time(), remainingTime);
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
     * <p>
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

        return switch (rateLimit.limitType()) {
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