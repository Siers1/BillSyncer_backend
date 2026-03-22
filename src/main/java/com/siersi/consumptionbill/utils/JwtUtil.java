package com.siersi.consumptionbill.utils;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.siersi.consumptionbill.exception.BusinessException;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT令牌工具类
 * 提供JWT令牌的生成、验证、解析等功能，用于用户身份认证
 * 
 * @author siersi
 * @version 1.0
 */
@Component
public class JwtUtil {
    
    /**
     * JWT签名密钥，从配置文件中读取
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private int accessTokenExpiration; // 分钟

    @Value("${jwt.refresh-token-expiration}")
    private int refreshTokenExpiration; // 天

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成双token对
     * 
     * @param account 用户账号，作为令牌的主题
     * @return 包含访问令牌和刷新令牌的Map
     */
    public Map<String, Object> generateTokens(String account) {
        // 1. 生成新token
        String accessToken = createToken(account, accessTokenExpiration, "access");
        String refreshToken = createToken(account, refreshTokenExpiration * 24 * 60, "refresh"); // 转换为分钟
        
        // 2. 存储到Redis
        stringRedisTemplate.opsForValue().set("token:" + account, accessToken, accessTokenExpiration, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set("refresh:" + account, refreshToken, refreshTokenExpiration, TimeUnit.DAYS);
        
        // 3. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    /**
     * 检查用户是否已有活跃登录
     * 
     * @param account 用户账号
     * @return 如果已有活跃登录返回true，否则返回false
     */
    public boolean hasActiveLogin(String account) {
        String existingToken = stringRedisTemplate.opsForValue().get("token:" + account);
        return existingToken != null;
    }

    /**
     * 生成双token对（强制登出模式）
     * 会先清除旧token，然后生成新token
     * 
     * @param account 用户账号，作为令牌的主题
     * @return 包含访问令牌和刷新令牌的Map
     */
    public Map<String, Object> generateTokensWithForceLogout(String account) {
        // 1. 先清除旧token（强制登出）
        clearOldTokens(account);

        return generateTokens(account);
    }


    /**
     * 创建token - 统一方法
     * 
     * @param account 用户账号
     * @param expiration 过期时间（分钟）
     * @param type token类型（access或refresh）
     * @return JWT token字符串
     */
    private String createToken(String account, int expiration, String type) {
        return JWT.create()
                .withSubject(account)
                .withClaim("type", type)
                .withExpiresAt(DateUtil.offsetMinute(new Date(), expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成JWT令牌（保持兼容性）
     * 
     * @param account 用户账号，作为令牌的主题
     * @return 生成的JWT令牌字符串
     */
    @Deprecated
    public String generateToken(String account) {
        return createToken(account, accessTokenExpiration, "access");
    }

    /**
     * 从JWT令牌中获取用户账号
     * 
     * @param token JWT令牌字符串
     * @return 令牌中包含的用户账号
     */
    public String getAccountFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getSubject();
    }

    /**
     * 验证访问token的有效性
     * 
     * @param token 访问token字符串
     * @return 如果令牌有效返回true，否则返回false
     */
    public boolean verifyAccessToken(String token) {
        try {
            // JWT验证
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            
            String account = jwt.getSubject();
            String type = jwt.getClaim("type").asString();
            
            // 检查类型和Redis中是否存在
            return "access".equals(type) && token.equals(stringRedisTemplate.opsForValue().get("token:" + account));
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证JWT令牌的有效性（保持兼容性）
     * 
     * @param token JWT令牌字符串
     * @return 如果令牌有效返回true，否则返回false
     */
    @Deprecated
    public boolean verifyToken(String token) {
        return verifyAccessToken(token);
    }

    /**
     * 刷新token
     * 
     * @param refreshToken 刷新令牌
     * @return 新的token对
     * @throws BusinessException 如果刷新令牌无效
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            // 验证refresh token
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(refreshToken);
            
            String account = jwt.getSubject();
            String type = jwt.getClaim("type").asString();
            
            // 检查类型和Redis中是否存在
            if (!"refresh".equals(type) || !refreshToken.equals(stringRedisTemplate.opsForValue().get("refresh:" + account))) {
                throw new BusinessException(401, "刷新令牌无效");
            }
            
            // 生成新的双token
            return generateTokens(account);
            
        } catch (Exception e) {
            throw new BusinessException(401, "刷新令牌无效");
        }
    }

    /**
     * 清除旧token - 单点登录核心
     * 
     * @param account 用户账号
     */
    private void clearOldTokens(String account) {
        stringRedisTemplate.delete("token:" + account);
        stringRedisTemplate.delete("refresh:" + account);
    }

    /**
     * 登出，清除所有token
     * 
     * @param account 用户账号
     */
    public void logout(String account) {
        clearOldTokens(account);
    }

    /**
     * 从Authorization请求头中提取JWT令牌
     * 
     * @param authorization Authorization请求头值，格式为"Bearer {token}"
     * @return 提取出的JWT令牌字符串
     * @throws BusinessException 如果Authorization头格式不正确或为空
     */
    public String extractTokenFromAuthorization(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new BusinessException(401, "请登录");
    }
}
