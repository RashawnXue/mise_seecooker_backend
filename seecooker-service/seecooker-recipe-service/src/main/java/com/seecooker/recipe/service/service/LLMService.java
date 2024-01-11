package com.seecooker.recipe.service.service;

import java.util.concurrent.CompletableFuture;

/**
 * 大模型服务层类
 *
 * @author xueruichen
 * @date 2024.01.11
 */
public interface LLMService {

    /**
     * 与大模型交流
     *
     * @param prompt prompt
     * @return 响应
     */
    String chat(String prompt);
}
