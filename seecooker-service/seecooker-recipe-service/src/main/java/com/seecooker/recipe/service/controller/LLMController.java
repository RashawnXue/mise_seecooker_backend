package com.seecooker.recipe.service.controller;

import com.seecooker.common.core.model.Result;
import com.seecooker.recipe.service.service.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大模型访问控制层
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Slf4j
@RestController
@RequestMapping("/v2/")
public class LLMController {
    private final LLMService llmService;

    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    /**
     * 大语言请求
     *
     * @param prompt prompt
     * @return 响应结果
     */
    @GetMapping("recipe/llm")
    public Result<String> getLLMResponse(@RequestParam String prompt) {
        String result = llmService.chat(prompt);
        return Result.success(result);
    }
}
