package com.seecooker.recipe.service.service.impl;

import com.seecooker.common.core.exception.BizException;
import com.seecooker.common.core.exception.ErrorType;
import com.seecooker.recipe.service.service.LLMService;
import com.unfbx.sparkdesk.SparkDeskClient;
import com.unfbx.sparkdesk.entity.*;
import com.unfbx.sparkdesk.listener.ChatListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 大模型服务层实现类
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Slf4j
@Service
public class LLMServiceImpl implements LLMService {
    private static final String SPARK_API_HOST_WSS_V3 = "https://spark-api.xf-yun.com/v3.1/chat";
    private static final String PROMPT = "你是一位精通营养学的厨师，请结合下面这道菜的原料，为我介绍一下这道菜的特点、功效等内容，尽量简洁一些，字数不要超过100";

    @Autowired
    private Environment environment;

    @Override
    public String chat(String prompt) {
        SparkDeskClient sparkDeskClient = SparkDeskClient.builder()
                .host(SPARK_API_HOST_WSS_V3)
                .appid(environment.getProperty("spark.appId"))
                .apiKey(environment.getProperty("spark.apiKey"))
                .apiSecret(environment.getProperty("spark.apiSecret"))
                .build();
        //构建请求参数
        InHeader header = InHeader.builder().uid(UUID.randomUUID().toString().substring(0, 10)).appid(environment.getProperty("spark.appId")).build();
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain("generalv3").maxTokens(2048).temperature(0.3).build()).build();
        List<Text> text = new ArrayList<>();
        text.add(Text.builder().role(Text.Role.USER.getName()).content(PROMPT + prompt).build());
        InPayload payload = InPayload.builder().message(Message.builder().text(text).build()).build();
        AIChatRequest aiChatRequest = AIChatRequest.builder().header(header).parameter(parameter).payload(payload).build();

        CompletableFuture<String> resultFuture = new CompletableFuture<>();
        StringBuilder resultBuilder = new StringBuilder();

        //发送请求
        sparkDeskClient.chat(new ChatListener(aiChatRequest) {
            //异常回调
            @SneakyThrows
            @Override
            public void onChatError(AIChatResponse aiChatResponse) {
                log.warn(String.valueOf(aiChatResponse));
                throw new BizException(ErrorType.LLM_ERROR);
            }

            //输出回调
            @Override
            public void onChatOutput(AIChatResponse aiChatResponse) {
                log.info("content: " + aiChatResponse);
                resultBuilder.append(aiChatResponse.getPayload().getChoices().getText().get(0).getContent().replace("\n", ""));
            }

            //会话结束回调
            @Override
            public void onChatEnd() {
                log.info("会话结束");
                resultFuture.complete(resultBuilder.toString());
            }

            //会话结束 获取token使用信息回调
            @Override
            public void onChatToken(Usage usage) {
                log.info("token 信息：" + usage);
            }
        });

        // 等待 CompletableFuture 完成
        try {
            return resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException(ErrorType.LLM_ERROR);
        }
    }
}
