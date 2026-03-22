package com.siersi.consumptionbill.controller;

import com.siersi.consumptionbill.dto.AI.AIRequest;
import com.siersi.consumptionbill.service.AI.AIService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Resource
    private AIService aiService;

    @PostMapping("/analyze")
    public SseEmitter AIAnalyze(@RequestBody AIRequest request) {
        SseEmitter emitter = new SseEmitter();

        // 启动一个异步线程来处理流式数据
        // 发生错误时关闭
        aiService.analyze(request)
                .doOnTerminate(emitter::complete)  // 流结束时自动关闭
                .doOnError(emitter::completeWithError)
                .subscribe(message -> {
                    try {
                        // 逐句发送消息到前端
                        emitter.send(message);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                });

        return emitter;  // 返回SseEmitter，进行流式推送
    }
}
