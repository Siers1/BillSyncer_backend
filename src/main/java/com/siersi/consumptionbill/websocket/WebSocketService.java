package com.siersi.consumptionbill.websocket;

import com.siersi.consumptionbill.service.User.UserService;
import com.siersi.consumptionbill.utils.JwtUtil;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/websocket/{token}", configurator = WebSocketConfigurator.class)
@RequiredArgsConstructor
public class WebSocketService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    private static final ConcurrentHashMap<Long, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        try {
            // 验证access token
            if (!jwtUtil.verifyAccessToken(token)) {
                session.close();
                log.warn("无效的访问令牌");
                return;
            }

            Long userId = userService.getIdByAccount(jwtUtil.getAccountFromToken(token));

            if (userId == null) {
                session.close();
                log.warn("无效的身份信息");
                return;
            }

            userSessions.put(userId, session);
            log.info("用户ID: {} 建立WebSocket连接成功", userId);

            session.getBasicRemote().sendText("连接成功！用户ID: " + userId);

        } catch (Exception e) {
            log.error("WebSocket连接失败{}", e.getMessage());
            try {
                session.close();
            } catch (IOException ioException) {
                log.error("关闭连接失败", ioException);
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("token") String token) {
        try {
            Long userId = userService.getIdByAccount(jwtUtil.getAccountFromToken(token));
            if (userId != null) {
                userSessions.remove(userId);
                log.info("用户ID: {} 断开WebSocket连接", userId);
            }
        } catch (Exception e) {
            log.error("处理断开连接失败", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) {
        try {
            Long userId = userService.getIdByAccount(jwtUtil.getAccountFromToken(token));
            
            // 处理心跳消息
            if ("ping".equals(message)) {
                log.debug("收到用户ID: {} 的心跳ping，回复pong", userId);
                session.getBasicRemote().sendText("pong");
                return;
            }

            // 对于其他消息，验证token
            if (!jwtUtil.verifyAccessToken(token)) {
                log.warn("用户ID: {} 的token无效，关闭连接", userId);
                session.close();
                return;
            }

            log.info("收到用户ID: {} 的消息: {}", userId, message);

        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    public static void sendMessage(Long userId, String message) {
        Session session = userSessions.get(userId);

        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("向用户 {} 发送消息: {}", userId, message);
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        } else {
            log.warn("用户 {} 不在线", userId);
        }
    }

    public static int getOnlineCount() {
        return userSessions.size();
    }
}