package com.seecooker.user.service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbitMQ配置类
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "modifySignature";

    @Bean
    public Queue favoriteQueue() {
        return new Queue(QUEUE_NAME);
    }
}
