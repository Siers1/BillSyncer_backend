package com.siersi.consumptionbill.service.AI.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.siersi.consumptionbill.config.AIConfig;
import com.siersi.consumptionbill.dto.AI.AIMessage;
import com.siersi.consumptionbill.dto.AI.AINetMessage;
import com.siersi.consumptionbill.dto.AI.AINetRequest;
import com.siersi.consumptionbill.dto.AI.AIRequest;
import com.siersi.consumptionbill.exception.BusinessException;
import com.siersi.consumptionbill.mapper.RecordMapper;
import com.siersi.consumptionbill.service.AI.AIService;
import com.siersi.consumptionbill.service.Record.RecordService;
import com.siersi.consumptionbill.service.User.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final AIConfig aiConfig;
    private final ObjectMapper objectMapper;
    private final RecordMapper recordMapper;

    @Override
    public Flux<AINetMessage> analyze(AIRequest request) {
        String apiUrl = aiConfig.getApiUrl();
        String apiKey = aiConfig.getApiKey();

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        AINetRequest netRequest = getAINetRequest(request);

        final int[] id = {1};
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(netRequest)
                .retrieve()
                .bodyToFlux(String.class)  // 以字符串形式接收响应
                .flatMap(response -> {
                    // 检查是否为结束标志 [DONE]
                    if ("[DONE]".equals(response.trim())) {
                        return Flux.just(new AINetMessage("[DONE]", id[0])); // 返回结束标志
                    }

                    try {
                        // 尝试解析 JSON 响应
                        JsonNode jsonResponse = objectMapper.readTree(response);

                        // 提取 content 字段
                        String content = jsonResponse.path("choices").get(0).path("delta").path("content").asText();
                        String reason_content = jsonResponse.path("choices").get(0).path("delta").path("reasoning_content").asText();

                        // 如果 content 包含 [DONE]，返回结束标志
                        if (content.contains("[DONE]")) {
                            return Flux.just(new AINetMessage("[DONE]", id[0]));
                        }

                        if (content.equals("null")) {
                            // 输出内容片段
                            System.out.print(reason_content);

                            if (id[0] == 1) {
                                AINetMessage netMessage = new AINetMessage("> " + reason_content, id[0]);
                                id[0] += 1;
                                return Flux.just(netMessage); // 返回内容片段
                            }else {
                                AINetMessage netMessage = new AINetMessage(reason_content, id[0]);
                                id[0] += 1;
                                return Flux.just(netMessage); // 返回内容片段
                            }


                        } else {
                            // 输出内容片段
                            System.out.print(content);

                            AINetMessage netMessage = new AINetMessage(content, id[0]);
                            id[0] += 1;
                            return Flux.just(netMessage); // 返回内容片段
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Flux.empty(); // 解析错误时返回空 Flux
                    }
                });
    }

    @Override
    public AINetRequest getAINetRequest(AIRequest request) {
        List<AIMessage> messageList = request.getMessageList();
        String model = aiConfig.getModel();

        AINetRequest netRequest = new AINetRequest();
        netRequest.setModel(model);
        netRequest.setStream(true);

        messageList = doPrompt(messageList, request.getBillId());

        netRequest.setMessages(messageList);
        return netRequest;
    }

    public List<AIMessage> doPrompt(List<AIMessage> messageList, Long billId) {
        String prompt1 = """
                你是一个专业的消费分析师。请基于以下消费记录数据，为用户提供个性化的消费建议：
                
                消费记录信息：
                - 消费项目：{itemName}
                - 消费金额：{itemPrice}
                - 消费类型：{consumptionType}
                - 支付方式：{paymentMethod}
                - 消费日期：{consumptionDate}
                - 备注：{comment}
                
                请从以下几个维度分析：
                1. 消费合理性分析
                2. 同类消费对比
                3. 消费频率建议
                4. 预算优化建议
                5. 替代方案推荐
                
                回答要求：
                - 提供具体可行的建议, 分点markdown形式
                - 没必要每条消费记录都分析, 结合全部给出综合分析就行
                - 结合消费时间和现在的时间
                - 控制在200字以内, 末尾不要给出多少字
                - 不要出现其他记账之类的账本工具软件, 因为我自己这个就是一个账本工具
                - 如果下方给你的数据中不为空就按照提示词回答, 如果没有数据的话就让用户去消费之类的填充一下账本, 灵活变通
                下面是账本数据:
                """;

        QueryWrapper queryWrapper = QueryWrapper.create().from("record")
                .where("bill_id = ?", billId).and("valid = 1");

        String prompt2 = recordMapper.selectListByQuery(queryWrapper).toString();

        String prompt3 = "\n当前时间: " + LocalDate.now();

        String content = prompt1 + prompt2 + prompt3;

        System.out.println(content);
        messageList.getFirst().setContent(content);

        return messageList;
    }
}
