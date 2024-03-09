package com.seecooker.community.service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
    public static final String QUEUE_NAME = "likePostQueue";

    @Bean
    public Queue likePostQueue() {
        return new Queue(QUEUE_NAME);
    }

    /**
     * 定义消息转换实例 ，转化成 JSON传输
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter integrationEventMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
